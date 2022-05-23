package org.paasta.container.platform.api.clusters.clusters;

import lombok.Data;

/**
 * Clusters Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.04
 **/
@Data
public class Clusters {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private long id;
    private String clusterName;
    private String clusterApiUrl;
    private String clusterToken;
    private String created;
}
