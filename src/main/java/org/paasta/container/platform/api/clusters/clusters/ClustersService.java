package org.paasta.container.platform.api.clusters.clusters;

import org.paasta.container.platform.api.clusters.clusters.support.ClusterInfo;
import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.overview.support.Count;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.pods.PodsList;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public ClustersService(RestTemplateService restTemplateService, VaultService vaultService, PropertyService propertyService, CommonService commonService, NodesService nodesService, PodsService podsService, UsersService usersService) {
        this.restTemplateService = restTemplateService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.nodesService = nodesService;
        this.podsService = podsService;
        this.usersService = usersService;
    }


    /**
     * Clusters 생성(Create Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters createClusters(Params params) {
        Clusters clusters = setClusters(params);
        if (params.getIsClusterRegister() && !createClusterInfoToVault(params)) {
            clusters.setResultMessage("createClusterInfoToVault Failed");
            return (Clusters)commonService.setResultModel(clusters, Constants.RESULT_STATUS_FAIL);
        }

        if (!params.getIsClusterRegister()) {
            //FIXME!! TERRAMAN API call
        }
        return (Clusters) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, params), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Clusters 정보 조회(Get Clusters Info)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters getClusters(Params params) {
        return (Clusters) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + params.getCluster(), HttpMethod.GET, null, Clusters.class, params), Constants.RESULT_STATUS_SUCCESS);
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
            try {
                NodesList nodesList = nodesService.getNodesList(new Params(clusters.getClusterId(), clusters.getName())); //status check

                if (nodesList.getItems().stream().filter(x -> x.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()).size() > 0) {
                    clusters.setIsActive(true);
                }
            } catch (Exception e) {
                LOGGER.info("error from getClustersList, " + e.getMessage());
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
        return (Clusters) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters" , HttpMethod.PUT, setClusters(params), Clusters.class, params), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 삭제(Delete Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters deleteClusters(Params params) {
        return restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/{id}".replace("{id}", params.getCluster()) , HttpMethod.DELETE, null, Clusters.class, params);
    }

    /**
     * Clusters 값 설정 (Set Clusters)
     *
     * @param params the params
     * @return the clusters
     */
    public Clusters setClusters(Params params) {
        Clusters clusters = new Clusters();
        clusters.setClusterId(params.getCluster());
        clusters.setClusterApiUrl(params.getClusterApiUrl());
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
        if (vaultService.getClusterDetails(params.getCluster()) != null) {
            LOGGER.info("cluster is already registered");
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
}