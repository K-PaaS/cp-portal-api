package org.paasta.container.platform.api.workloads.pods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.workloads.pods.support.PodsStatus;
import org.paasta.container.platform.api.workloads.pods.support.Volume;


import java.util.List;

/**
 * Pods Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.09
 */
@Data
public class PodsAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private String namespace;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;

    private String nodes;
    private String podStatus;
    private String ip;
    private String qosClass;
    private int restarts;
    private String controllers;
    private String volumes;
    private String containersName;
    private String containersImage;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private PodsStatus status;

    public int getRestarts() {
        return (int) Math.floor(status.getContainerStatuses().get(0).getRestartCount());
    }

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public String getNodes() { return CommonUtils.procReplaceNullValue(spec.getNodeName()); }

    public String getIp() {
        return status.getPodIP();
    }

    public String getQosClass() {
        return status.getQosClass();
    }

    public String getControllers() {
        if(metadata.getOwnerReferences() == null) {return Constants.NULL_REPLACE_TEXT;}
        return CommonUtils.procReplaceNullValue(metadata.getOwnerReferences().get(0).getName());
    }

    public List<Volume> getVolumes() {
        return spec.getVolumes();
    }

    public String getContainersName() {
        return spec.getContainers().get(0).getName();
    }

    public String getContainersImage() {
        return spec.getContainers().get(0).getImage();
    }

    public String getPodStatus() {
        return status.getPhase();
    }
}
