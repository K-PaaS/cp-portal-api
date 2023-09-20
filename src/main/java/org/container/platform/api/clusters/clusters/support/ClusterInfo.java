package org.container.platform.api.clusters.clusters.support;

import lombok.Data;

/**
 * ClusterInfo Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class ClusterInfo {
    private String clusterId;
    private String clusterApiUrl;
    private String clusterToken;
}
