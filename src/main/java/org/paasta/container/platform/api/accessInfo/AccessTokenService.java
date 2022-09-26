package org.paasta.container.platform.api.accessInfo;

import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * Access Token Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.29
 */
@Service
public class AccessTokenService {
    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private final VaultService vaultService;

    /**
     * Instantiates a new AccessToken service
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     * @param commonService       the common service
     */
    @Autowired
    public AccessTokenService(RestTemplateService restTemplateService, PropertyService propertyService, CommonService commonService, VaultService vaultService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.vaultService = vaultService;
    }



    /**
     * Vault Secrets 상세 조회(Get Vault Secrets detail)
     *
     * @param params the params
     */
    public Params getVaultSecrets(Params params) {
        Assert.hasText(params.getCluster(), "cluster id not null");
        Assert.hasText(params.getUserType(), "userType must valid");

        String clusterId = params.getCluster();

        Clusters clusters = vaultService.getClusterDetails(clusterId);
        Clusters detailsClusters = vaultService.getClusterInfoDetails(params);
        System.out.println("detailsClusters = " + detailsClusters);
        params.setCluster(clusterId);
        params.setClusterApiUrl(clusters.getClusterApiUrl());
        params.setClusterToken(vaultService.getClusterInfoDetails(params).getClusterToken());

//        if (params.getUserType().equals("SUPER_ADMIN")) {
//            Clusters clusters = vaultService.getClusterInfoDetails(params);
//            apiUrl = clusters.getClusterApiUrl();
//            id = clusters.getClusterId();
//            token = clusters.getClusterToken();
//        } else if (params.getUserType().equals("CLUSTER_ADMIN")) {
//            Clusters clusters = vaultService.getClusterInfoDetails(params.getUserAuthId(), params.getCluster());
//            apiUrl = clusters.getClusterApiUrl();
//            id = clusters.getClusterId();
//            token = clusters.getClusterToken();
//        } else if (params.getUserType().equals("USER")) {
//            Clusters clusters = vaultService.getClusterInfoDetails(params.getUserAuthId(), params.getCluster(), params.getNamespace());
//            apiUrl = clusters.getClusterApiUrl();
//            id = clusters.getClusterId();
//            token = clusters.getClusterToken();
//        }

//        params.setClusterApiUrl(apiUrl);
//        params.setCluster(id);
//        params.setClusterToken(token);

        return params;
    }

}
