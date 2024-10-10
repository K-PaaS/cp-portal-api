package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.10
 **/
@Data
public class AuthKubernetesRoles {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private AuthKubernetesRolesData data;
}
