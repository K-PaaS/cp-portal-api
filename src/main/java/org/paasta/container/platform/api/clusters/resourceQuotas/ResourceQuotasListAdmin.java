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
 * ResourceQuotas List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ResourceQuotasListAdminItem> items;

}

@Data
class ResourceQuotasListAdminItem {
    @JsonIgnore
    public ResourceQuotasStatus status;
    private String name;
    private String namespace;
    private String creationTimestamp;

    @JsonIgnore
    private Map<String, Object> convertStatus;

    @JsonIgnore
    private CommonMetaData metadata;

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getName() {
        return name = metadata.getName();
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

    public CommonMetaData getMetadata() {
        return (StringUtils.isEmpty(metadata)) ? new CommonMetaData() {{
            setName(Constants.NULL_REPLACE_TEXT);
        }} : metadata;
    }
}

