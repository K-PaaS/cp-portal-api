package org.paasta.container.platform.api.clusters.clusters;

import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Clusters Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.04
 **/
@Service
public class ClustersService {
    private final RestTemplateService restTemplateService;

    /**
     * Instantiates a new Clusters service
     *
     * @param restTemplateService the rest template service
     */
    @Autowired
    public ClustersService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }


    /**
     * Clusters 정보 저장(Create Clusters Info)
     *
     * @param clusterApiUrl the cluster api url
     * @param clusterName   the cluster name
     * @param clusterToken  the cluster token
     * @return the clusters
     */
    public Clusters createClusters(String clusterApiUrl, String clusterName, String clusterToken) {
        Clusters clusters = new Clusters();
        clusters.setClusterApiUrl(clusterApiUrl);
        clusters.setClusterName(clusterName);
        clusters.setClusterToken(clusterToken);

        return restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class);
    }


    /**
     * Clusters 정보 조회(Get Clusters Info)
     *
     * @param clusterName the cluster name
     * @return the clusters
     */
    public Clusters getClusters(String clusterName) {
        return restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters/" + clusterName, HttpMethod.GET, null, Clusters.class);
    }
}
