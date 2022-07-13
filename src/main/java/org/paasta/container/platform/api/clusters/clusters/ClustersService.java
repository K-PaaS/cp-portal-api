package org.paasta.container.platform.api.clusters.clusters;

import org.paasta.container.platform.api.clusters.clusters.support.ClusterInfo;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

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
    public ClustersService(RestTemplateService restTemplateService, VaultService vaultService, PropertyService propertyService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.commonService = commonService;
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

        ClustersList clustersList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.GET, null, ClustersList.class, params);
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
        clusters.setClusterId(params.getCluster()); //FIXME! clusterId?
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
        //FIXME, clusterId 유효성검사?(중복제거)
        if (params.getCluster().equals("cp-cluster")) {
            LOGGER.info("Invalid clusterId, cp-cluster can't be created");
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