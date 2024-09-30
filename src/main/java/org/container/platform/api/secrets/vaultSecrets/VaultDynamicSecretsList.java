package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;
import java.util.List;

/**
 * VaultDynamicSecrets 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class VaultDynamicSecretsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private List<VaultDynamicSecrets> items;
}
