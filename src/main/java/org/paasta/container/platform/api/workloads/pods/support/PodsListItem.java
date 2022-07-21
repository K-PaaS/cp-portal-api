package org.paasta.container.platform.api.workloads.pods.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;

import java.util.List;

@Data
public class PodsListItem {
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


    public Integer getRestarts() {
        return status.getContainerStatuses().get(0).getRestartCount();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }


    //If a container is not in either the Running or Terminated state, it is Waiting
    public String getPodStatus() {
        List<ContainerStatusesItem> containerStatuses = status.getContainerStatuses();
        for (ContainerStatusesItem cs : containerStatuses) {

            if (cs.getState().containsKey(Constants.CONTAINER_STATE_WAITING)) {
                return cs.getState().get(Constants.CONTAINER_STATE_WAITING).getReason();
            }
            if (cs.getState().containsKey(Constants.CONTAINER_STATE_TERMINATED)) {
                return cs.getState().get(Constants.CONTAINER_STATE_TERMINATED).getReason();
            }
        }
        // container running
        return status.getPhase();
    }


}
