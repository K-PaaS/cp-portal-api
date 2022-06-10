package org.paasta.container.platform.api.clusters.clusters;

import lombok.Data;

/**
 * Clusters Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.09
 **/
@Data
public class Clusters {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String clusterId;
    private String clusterApiUrl;
    private String clusterName;
    private String clusterToken;
    private String clusterType;
    private String created;
    private String lastModified;
}