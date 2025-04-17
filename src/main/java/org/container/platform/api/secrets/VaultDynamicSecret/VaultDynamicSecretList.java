package org.container.platform.api.secrets.VaultDynamicSecret;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * Service Account List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.12.07
 **/
@Data
public class VaultDynamicSecretList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String apiVersion;
    private String kind;
    private Map metadata;
    private List<VaultDynamicSecret> items;
    private CommonItemMetaData itemMetaData;
}
