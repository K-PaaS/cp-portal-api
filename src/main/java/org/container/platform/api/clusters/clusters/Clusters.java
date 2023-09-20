package org.container.platform.api.clusters.clusters;

import lombok.Data;
import org.container.platform.api.common.Constants;
import org.container.platform.api.overview.support.Count;

/**
 * Clusters Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class Clusters {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String userAuthId;
    private String namespace;

    private String clusterId;
    private String clusterApiUrl;
    private String name;
    private String clusterToken;
    private String clusterType;
    private Constants.ProviderType providerType;
    private String description;
    private String kubernetesVersion;
    private Count NodeCount;
    private Count podCount;
    private String created;
    private String lastModified;
    private Boolean isActive = false;
    private String status = Constants.ClusterStatus.DISABLED.getInitial();
}