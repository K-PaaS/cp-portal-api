package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasSpec;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatusItem;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonMetaData;

/**
 * ResourceQuotas Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String namespace;
    private List<String> scopes;
    private String creationTimestamp;

    private List<ResourceQuotasStatusItem> items;

    @JsonIgnore
    private ResourceQuotasStatus status;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private ResourceQuotasSpec spec;

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getName() {
        return name = metadata.getName();
    }

    public List<?> getScopes() {
        return CommonUtils.procReplaceNullValue(spec.getScopes(), Constants.ListObjectType.STRING);
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }
}
