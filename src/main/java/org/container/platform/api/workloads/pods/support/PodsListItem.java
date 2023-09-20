package org.container.platform.api.workloads.pods.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;

import java.util.List;
import java.util.Map;

@Data
public class PodsListItem {
    private String name;
    private String namespace;
    private Object labels;
    private String nodes;
    private String podStatus;
    private Integer restarts;
    private String creationTimestamp;
    private String phase;
    private String containerStatus;

    private Map<String, Object> cpu = Constants.INIT_USAGE;
    private Map<String, Object> memory = Constants.INIT_USAGE;


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

    public String getPhase() {
        return status.getPhase();
    }

    //If a container is not in either the Running or Terminated state, it is Waiting
    public String getPodStatus() {
        return findPodStatus(status.getContainerStatuses());
    }

    public String findPodStatus(List<ContainerStatusesItem> containerStatuses) {
        for (ContainerStatusesItem cs : containerStatuses) {
            setContainerStatus(getPhase());
            try {
                if (cs.getState().containsKey(Constants.CONTAINER_STATE_WAITING)) {
                    // waiting
                    if (getPhase().equalsIgnoreCase(Constants.STATUS_RUNNING)) {
                        setContainerStatus(Constants.STATUS_FAILED);
                    }
                    return cs.getState().get(Constants.CONTAINER_STATE_WAITING).getReason();
                }
                if (cs.getState().containsKey(Constants.CONTAINER_STATE_TERMINATED)) {
                    // terminated
                    return cs.getState().get(Constants.CONTAINER_STATE_TERMINATED).getReason();
                }
            } catch (Exception e) {
                return (status.getReason() != null) ? status.getReason() : status.getPhase();

            }
        }
        // container running
        return status.getPhase();
    }
}
