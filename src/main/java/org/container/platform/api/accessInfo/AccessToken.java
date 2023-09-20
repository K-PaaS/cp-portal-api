package org.container.platform.api.accessInfo;

import lombok.Data;

/**
 * Access Token Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.29
 */
@Data
public class AccessToken {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String caCertToken;
    private String userAccessToken;
    private String userAuthId;
    private String userType;
    private String userId;

    public String clusterApiUrl;
    public String clusterId;
    public String clusterToken;
}
