package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.List;
import java.util.Map;


import lombok.Data;

import org.springframework.util.StringUtils;

import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;

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
