package org.paasta.container.platform.api.clusters.namespaces.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;


/**
 * Namespaces List Items Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NamespacesListItem {
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
