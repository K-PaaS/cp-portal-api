package org.container.platform.api.common;

import io.jsonwebtoken.lang.Assert;
import org.container.platform.api.clusters.clusters.Clusters;
import org.container.platform.api.common.model.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VaultService {

    @Autowired
    VaultTemplate vaultTemplate;

    @Autowired
    PropertyService propertyService;

    @Autowired
    CommonService commonService;

    @Autowired
    TemplateService templateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultService.class);

    /**
     * Vault read를 위한 method
     *
     * @param path the path
     * @return the object
     */
    @TrackExecutionTime
    public <T> T read(String path, Class<T> requestClass) {

        if (path.contains("database")) {

            Object response = Optional.of(vaultTemplate.read(path));

            return commonService.setResultObject(response, requestClass);
        } else {
            path = setPath(path);

            Object response2 = Optional.of(vaultTemplate.read(path)).map(VaultResponse::getData).filter(x -> x.containsKey("data")).orElseGet(HashMap::new).getOrDefault("data", null);

            return commonService.setResultObject(response2, requestClass);
        }

    }

    /**
     * Vault write를 위한 method
     *
     * @param path the path
     * @return the object
     */
    @TrackExecutionTime
    public Object write(String path, Object body) {

        if (path.contains("database") || path.contains("policies") || path.contains("auth")) {

            return vaultTemplate.write(path, body);
        } else {
            path = setPath(path);

            Map<String, Object> data = new HashMap<>();
            data.put("data", body);

            return vaultTemplate.write(path, data);
        }
    }

    /**
     * Vault delete를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public void delete(String path) {
        if (path.contains("database") || path.contains("policies") || path.contains("auth")) {
            vaultTemplate.delete(path);
        } else {
            path = setPath(path);
            vaultTemplate.delete(path);
        }

    }

    /**
     * Vault path 처리를 위한 method
     *
     * @param path the path
     * @return the String
     */
    private String setPath(String path) {
        return new StringBuilder(path).insert(path.indexOf("/") + 1, "data/").toString();
    }


    /**
     * Vault를 통한 Cluster 정보 조회
     *
     * @param clusterId the clusterId
     * @return the String
     */
    public Clusters getClusterDetails(String clusterId) {
        Assert.hasText(clusterId);
        Clusters cluster = null;
        try {
            cluster = read(propertyService.getVaultSecretsEnginesKvClusterTokenPath().replace("{id}", clusterId), Clusters.class);
        }
        catch (NullPointerException e) {
            LOGGER.info("No cluster details registered with this clusterId >> " + e.getMessage());
        }
        catch (Exception e) {
            LOGGER.info("exception occurred while getting cluster details >> " + e.getMessage());
        }
        return cluster;
    }


    /**
     * Vault를 통한 Super Admin Cluster 정보 조회
     *
     * @param params the params
     * @return the String
     */
    public Clusters getClusterInfoDetails(Params params) {
        Assert.hasText(params.getCluster());
        Assert.hasText(params.getUserType());
        String userType = params.getUserType();

        if (!userType.equals(Constants.AUTH_SUPER_ADMIN)) Assert.hasText(params.getUserAuthId());
        if (userType.equals(Constants.AUTH_USER)) Assert.hasText(params.getNamespace());

        return read(getAccessTokenPath(params), Clusters.class);
    }

    public void saveUserAccessToken(Params params) {
        Map<String, Object> token = new HashMap<>();
        token.put("clusterToken", params.getSaToken());
        write(getAccessTokenPath(params), token);
    }


    public void deleteUserAccessToken(Params params) {
        delete(getAccessTokenPath(params));
    }

    public String getAccessTokenPath(Params params) {
        String userType = params.getUserType();
        String tokenPath;

        switch (userType) {
            case Constants.AUTH_SUPER_ADMIN:
                tokenPath = propertyService.getVaultSecretsEnginesKvSuperAdminTokenPath().replace("{clusterId}", params.getCluster());
                break;
            case Constants.AUTH_CLUSTER_ADMIN:
            case Constants.AUTH_USER:
                tokenPath = propertyService.getVaultSecretsEnginesKvUserTokenPath().replace("{userAuthId}", params.getUserAuthId()).replace("{clusterId}", params.getCluster());
                tokenPath = params.getUserType().equalsIgnoreCase(Constants.AUTH_CLUSTER_ADMIN) ? tokenPath.replace("/{namespace}", "") : tokenPath.replace("{namespace}", params.getNamespace());
                break;
            default:
                //WARN, Invalid userType
                tokenPath = null;
        }

        return tokenPath;
    }

}
