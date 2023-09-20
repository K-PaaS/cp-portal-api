package org.container.platform.api.adminToken;

import lombok.Data;

/**
 * Admin Token Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.31
 */
@Data
public class AdminToken {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String tokenName;
    private String tokenValue;
    private int statusCode;

}
