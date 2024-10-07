package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseCredentials 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class DatabaseCredentialsData {
    private String username;
    private String password;
}
