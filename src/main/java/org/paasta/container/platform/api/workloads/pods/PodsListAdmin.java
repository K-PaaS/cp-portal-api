package org.paasta.container.platform.api.workloads.pods;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.workloads.pods.support.PodsStatus;

/**
 * Pods List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.09
 */
@Data
public class PodsListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PodsListAdminList> items;
}

@Data
class PodsListAdminList {
    private String name;
    private String namespace;
    private Object labels;
    private String nodes;
    private String podStatus;
    private Integer restarts;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private PodsStatus status;


    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getNodes() {
        return CommonUtils.procReplaceNullValue(spec.getNodeName());
    }

    public String getPodStatus() {
        return status.getPhase();
    }

    public Integer getRestarts() {
        return status.getContainerStatuses().get(0).getRestartCount();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }
}
