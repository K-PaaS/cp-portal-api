package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;
import java.util.List;

/**
 * VaultDatabaseSecrets Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Data
public class VaultDatabaseSecretsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private List<VaultDatabaseSecrets> items;
}
