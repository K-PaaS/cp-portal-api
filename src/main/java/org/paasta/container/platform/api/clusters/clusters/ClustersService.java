package org.paasta.container.platform.api.clusters.clusters;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.paasta.container.platform.api.clusters.cloudAccounts.CloudAccountsService;
import org.paasta.container.platform.api.clusters.clusters.support.*;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.CommonStatusCodeException;
import org.paasta.container.platform.api.exception.ResultStatusException;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clusters Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/

@Service
public class ClustersService {
    private final RestTemplateService restTemplateService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private final NodesService nodesService;
    private final PodsService podsService;
    private final UsersService usersService;
    private final CloudAccountsService cloudAccountsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClustersService.class);

    /**
     * Instantiates a new Clusters service
     *
     * @param restTemplateService the rest template service
     * @param vaultService the vault service
     * @param propertyService the property service
     * @param commonService the common service
     */
    @Autowired
    public ClustersService(RestTemplateService restTemplateService, VaultService vaultService, PropertyService propertyService, CommonService commonService, NodesService nodesService, PodsService podsService, UsersService usersService, CloudAccountsService cloudAccountsService) {
        this.restTemplateService = restTemplateService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.nodesService = nodesService;
        this.podsService = podsService;
        this.usersService = usersService;
        this.cloudAccountsService = cloudAccountsService;
    }


    /**
     * Clusters 생성(Create Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    @SuppressFBWarnings
    public Clusters createClusters(Params params) {
        Clusters clusters = setClusters(params);
        Clusters ret;

        if (ObjectUtils.isEmpty(params) || ObjectUtils.isEmpty(params.getCluster())) {
            throw new ResultStatusException(MessageConstant.INVALID_NAME_FORMAT.getMsg());
        }

        Clusters checkedClusters = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + params.getCluster(), HttpMethod.GET, null, Clusters.class, params);
        if(checkedClusters != null) {
            throw new ResultStatusException(CommonStatusCode.CONFLICT.getMsg());
        }

        if (!params.getIsClusterRegister()) {
            if(ObjectUtils.isEmpty(params.getCloudAccountId()) ||
                    cloudAccountsService.getCloudAccounts(new Params(params.getCloudAccountId())).getResultCode().equals(Constants.RESULT_STATUS_FAIL))
                throw new ResultStatusException(MessageConstant.RE_CONFIRM_INPUT_VALUE.getMsg());
            //Create Files
                String path = propertyService.getCpTerramanTemplatePath().replace("{id}", params.getCluster());
                Path filePath = Paths.get(path);
            try {
                Files.createDirectories(filePath.getParent());
            } catch (Exception e) {
                LOGGER.info("Template directory create Error : " + CommonUtils.loggerReplace(e.getMessage()));
                return clusters;
            }

            try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path)) ) {
                LOGGER.info("File write Start : " + CommonUtils.loggerReplace(path));
                bufferedOutputStream.write(params.getHclScript().getBytes());
                LOGGER.info("File write End");

            } catch (Exception e) {
                LOGGER.info("Template file write Error");
                LOGGER.info("Error Message: " + CommonUtils.loggerReplace(e.getMessage()));
                throw new ResultStatusException(MessageConstant.CODE_ERROR.getMsg());
            }
            //DB Write
            ret = (Clusters) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, params), Constants.RESULT_STATUS_SUCCESS);

            //Setting arguments
            TerramanParams terramanParams = new TerramanParams();
            terramanParams.setClusterId(params.getCluster());
            terramanParams.setProvider(params.getProviderType().name());
            terramanParams.setSeq(params.getCloudAccountId());
            LOGGER.info("Terraman API call Start : " + CommonUtils.loggerReplace(terramanParams.toString()));

            try {
                restTemplateService.sendGlobal(Constants.TARGET_TERRAMAN_API, "/clusters/create/container", HttpMethod.POST, terramanParams, TerramanParams.class, params);
            } catch (RuntimeException e) {
                LOGGER.info("Terraman API call Error");
                this.deleteClusters(params);
                throw new ResultStatusException(MessageConstant.UNABLE_TO_COMMUNICATE_TERRAMAN_SERVER.getMsg());
            }

            LOGGER.info("Terraman API call end");
        }
        else {

            Clusters rgCluster = new Clusters();

            try{
                restTemplateService.sendValid(params.getClusterApiUrl(), HttpMethod.GET,  Map.class, params);
                createClusterInfoToVault(params);
                clusters.setStatus(Constants.ClusterStatus.ACTIVE.getInitial());
                restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, params);
            }
            catch (CommonStatusCodeException e) {
                LOGGER.info("CLUSTER_REGISTRATION_FAILED : " + CommonUtils.loggerReplace(e.getErrorMessage()));
                throw new CommonStatusCodeException(e.getErrorMessage());
            }
            catch(Exception e){
                LOGGER.info("CLUSTER_REGISTRATION_FAILED : " + CommonUtils.loggerReplace(e.getMessage()));
                throw new ResultStatusException(MessageConstant.CLUSTER_REGISTRATION_FAILED.getMsg());
            }

            return (Clusters) commonService.setResultModel(rgCluster, Constants.RESULT_STATUS_SUCCESS);
        }

        return ret;
    }


    /**
     * Clusters 정보 조회(Get Clusters Info)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters getClusters(Params params) {
        // get k8s Info
        Clusters clusters = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + params.getCluster(), HttpMethod.GET, null, Clusters.class, params);
        if (ObjectUtils.isEmpty(clusters)) {
            throw new ResultStatusException(MessageConstant.NOT_EXIST_RESOURCE.getMsg());
        }
        try {
            Clusters vaultClusters = vaultService.getClusterDetails(params.getCluster());
            clusters.setClusterApiUrl(vaultClusters.getClusterApiUrl());
            clusters.setClusterToken(vaultClusters.getClusterToken());
        } catch (Exception e) {
            LOGGER.info("not exist vault info");
        }

        clusters.setKubernetesVersion(Constants.noName);
        if (clusters.getStatus().equals(Constants.ClusterStatus.ACTIVE.getInitial())) {
            try {
                restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, params); // check ping
                clusters.setKubernetesVersion(
                        nodesService.getNodesList(new Params(clusters.getClusterId(), clusters.getName())).getItems().stream()
                        .filter(x -> x.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                        .get(0).getStatus().getNodeInfo().getKubeletVersion());
                clusters.setIsActive(true);
            } catch (Exception e) {
                LOGGER.info("Cluster is not Active");
                clusters.setIsActive(false);
            }
        }
        return (Clusters) commonService.setResultModel(clusters, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 목록 조회(Get Clusters List)
     *
     * @param params the params
     * @return the clusters list
     */
    public ClustersList getClustersList(Params params) {

        ClustersList clustersList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/users/{userAuthId}?userType={userType}"
                .replace("{userAuthId}", params.getUserAuthId())
                .replace("{userType}", params.getUserType()), HttpMethod.GET, null, ClustersList.class, params);

        for (Clusters clusters : clustersList.getItems()) {
            if(!clusters.getStatus().equals(Constants.ClusterStatus.ACTIVE.getInitial())){
//                clusters.setIsActive(false);
                continue;
            }
            try {
                params.setCluster(clusters.getClusterId());
                ClusterPing clusterPing = restTemplateService.sendGlobal(Constants.TARGET_METRIC_COLLECTOR_API, "/v1/metrics/cluster/ping/{clusterId}"
                        .replace("{clusterId}", params.getCluster()), HttpMethod.GET, null, ClusterPing.class, params);
                LOGGER.info("resultCode: " + clusterPing.getStatus_code());
                if(clusterPing.getStatus_code() == 200) {
                    clusters.setIsActive(true);
                } else {
                    clusters.setIsActive(false);
                }
            } catch (Exception e) {
                LOGGER.info("error from getClustersList, " + CommonUtils.loggerReplace(e.getMessage()));
                clusters.setIsActive(false);
            }

        }
/* get version, nodes, pods from service
        clustersList.getItems().stream().forEach(
                (e) -> e.setKubernetesVersion(
                        nodesService.getNodesList(new Params(e.getClusterId(), e.getName())).getItems().stream()
                                .filter(x -> x.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane"))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                                .get(0).getStatus().getNodeInfo().getKubeletVersion()));

        clustersList.getItems().stream().forEach(
                (e) -> e.setNodeCount(
                        new Count(nodesService.getNodesList(new Params(e.getClusterId(), e.getName()))
                                .getItems().stream()
                                .filter(x -> x.getReady().equalsIgnoreCase("True")).collect(Collectors.toList()).size(),
                                nodesService.getNodesList(new Params(e.getClusterId(), e.getName())).getItems().size())));

        clustersList.getItems().stream().forEach(
                (e) -> e.setPodCount(
                        new Count(podsService.getPodsList(new Params(e.getClusterId(), e.getName()))
                                .getItems().stream()
                                .filter(x -> x.getPodStatus().equalsIgnoreCase("Running")).collect(Collectors.toList()).size(),
                                podsService.getPodsList(new Params(e.getClusterId(), e.getName())).getItems().size())));
 */

//        clustersList.getItems().stream().forEach(x -> LOGGER.info("getAuthorityFromContext Test :: Cluster: " + x.getClusterId() +
//                " Authority : " + commonService.getClusterAuthorityFromContext(x.getClusterId())));

        clustersList = commonService.globalListProcessing(clustersList, params, ClustersList.class);
        return (ClustersList) commonService.setResultModel(clustersList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 수정(Update Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters updateClusters(Params params) {
        return (Clusters) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters" , HttpMethod.PATCH, setClusters(params), Clusters.class, params), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 삭제(Delete Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters deleteClusters(Params params) {
        UsersList usersList;

        try {
            usersList = usersService.getUsersListByCluster(params);
        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        if (usersList.getClusterType().equalsIgnoreCase(Constants.CLUSTER_TYPE_HOST)) {
            throw new ResultStatusException(MessageConstant.DO_NOT_DISCONNECTED_HOST_CLUSTER.getMsg());
        }

        try {
            restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters/{id}".replace("{id}", params.getCluster()),
                    HttpMethod.DELETE, null, ResultStatus.class, params);

            vaultService.delete(propertyService.getVaultClusterTokenPath().replace("{id}", params.getCluster()));

            for (Users users : usersList.getItems()) {
                Params deleteUser = new Params(users.getClusterId(), users.getUserAuthId(), users.getUserType(), users.getCpNamespace());
                vaultService.deleteUserAccessToken(deleteUser);
            }

        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        return (Clusters) commonService.setResultModel(new Clusters(), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 값 설정 (Set Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters setClusters(Params params) {
        Clusters clusters = new Clusters();
        this.validationCheckCluster(params);
        clusters.setClusterId(params.getCluster());
        clusters.setName(params.getResourceName());
        clusters.setClusterType(params.getClusterType());
        clusters.setProviderType(params.getProviderType());
        clusters.setDescription(params.getDescription());

        return clusters;
    }

    /**
     * Clusters Vault 생성 (Create Clusters Vault Info)
     *
     * @param params the params
     * @return the clusters
     */
    public boolean createClusterInfoToVault(Params params) {
        //Check ClusterId
        Clusters clusters = vaultService.getClusterDetails(params.getCluster());
        if(!Objects.isNull(clusters)) {
            LOGGER.info("cluster id is already exist.");
            return false;
        }

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(params.getCluster());
        clusterInfo.setClusterApiUrl(params.getClusterApiUrl());
        clusterInfo.setClusterToken(params.getClusterToken());

        try {
            vaultService.write(propertyService.getVaultClusterTokenPath().replace("{id}", params.getCluster()), clusterInfo);
        } catch (Exception e) {
            LOGGER.info("Vault Write failed in createClusterInfoToVault");
            return false;
        }

        return true;
    }

    private void validationCheckCluster(Params params) {
        if(StringUtils.isBlank(params.getCluster())
            || StringUtils.isBlank(params.getResourceName())
            || StringUtils.isBlank(params.getClusterType())
            || StringUtils.isBlank(String.valueOf(params.getProviderType()))
        ) {
            throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
        }
    }
}