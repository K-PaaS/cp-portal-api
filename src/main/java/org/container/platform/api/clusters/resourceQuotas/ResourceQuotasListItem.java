package org.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.model.CommonMetaData;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * ResourceQuotas List Item Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class ResourceQuotasListItem {
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
        return (ObjectUtils.isEmpty(metadata)) ? new CommonMetaData() {{
            setName(Constants.NULL_REPLACE_TEXT);
        }} : metadata;
    }
}