package org.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonSpec;

/**
 * ResourceQuotas Default List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Data
public class ResourceQuotasDefaultList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;

    private List<ResourceQuotasDefault> items;
}

@Data
class ResourceQuotasDefaultItem {
    private String name;
    private String status;
    private String checkYn;

    @JsonIgnore
    private CommonSpec spec;

}