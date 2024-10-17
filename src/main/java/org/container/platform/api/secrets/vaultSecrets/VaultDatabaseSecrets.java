package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * VaultDatabaseSecrets Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Data
public class VaultDatabaseSecrets {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private long id;
    private String name;
    private String dbType;
    private String appName;
    private String appNamespace;
    private String status;
    private String flag;
}
