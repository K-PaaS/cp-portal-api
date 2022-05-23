package org.paasta.container.platform.api.clusters.namespaces;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * Namespaces List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Data
public class NamespacesListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<NamespacesListAdminItem> items;
}

@Data
class NamespacesListAdminItem {
    private String name;
    private Object labels;
    private String namespaceStatus;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getNamespaceStatus() {
        return status.getPhase();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
