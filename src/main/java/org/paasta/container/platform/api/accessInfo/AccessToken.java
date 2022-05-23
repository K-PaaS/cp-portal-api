package org.paasta.container.platform.api.accessInfo;

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
}
