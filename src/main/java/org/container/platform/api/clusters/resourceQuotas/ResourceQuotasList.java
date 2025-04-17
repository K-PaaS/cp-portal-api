package org.container.platform.api.clusters.resourceQuotas;

import java.util.List;
import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;

/**
 * ResourceQuotas List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class ResourceQuotasList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ResourceQuotasListItem> items;
}
