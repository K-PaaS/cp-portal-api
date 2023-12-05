package org.container.platform.api.accessInfo;

import org.container.platform.api.clusters.clusters.Clusters;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
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
    private final VaultService vaultService;

    /**
     * Instantiates a new AccessToken service
     * @param vaultService  the vault service
     */
    @Autowired
    public AccessTokenService(VaultService vaultService) {
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
        params.setCluster(clusterId);
        params.setClusterApiUrl(clusters.getClusterApiUrl());
        params.setClusterToken(vaultService.getClusterInfoDetails(params).getClusterToken());

        return params;
    }

}
