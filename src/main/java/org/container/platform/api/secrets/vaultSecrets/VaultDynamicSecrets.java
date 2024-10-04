package org.container.platform.api.secrets.vaultSecrets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * VaultDynamicSecrets 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class VaultDynamicSecrets {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    @JsonIgnore
    private  VaultDynamicSecretsSpec spec;

    @JsonIgnore
    private  VaultDynamicSecretsMetadata metadata;

}