package org.paasta.container.platform.api.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Authentication Response Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AuthenticationResponse {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String userId;
    private String userAuthId;
    private String userType;
    private String token;
    private String clusterId;
    private Boolean isSuperAdmin;

    public AuthenticationResponse(String resultCode, String resultMessage, Integer httpStatusCode, String detailMessage, String token) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
        this.token = token;
    }
}