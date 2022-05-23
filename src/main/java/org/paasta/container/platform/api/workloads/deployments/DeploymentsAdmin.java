package org.paasta.container.platform.api.workloads.deployments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsSpec;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsStatus;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsStrategy;

import java.util.List;

/**
 * Deployments Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.11
 **/
@Data
public class DeploymentsAdmin {
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
    private DeploymentsStrategy strategy;
    private int minReadySeconds;
    private int revisionHistoryLimit;
    private Object selector;
    private int updated;
    private int total;
    private int available;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private DeploymentsSpec spec;

    @JsonIgnore
    private DeploymentsStatus status;

    public String getName() {
        return name = metadata.getName();
    }

    public String getUid() {
        return uid = metadata.getUid();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

    public DeploymentsStrategy getStrategy() {
        return strategy = spec.getStrategy();
    }

    public int getRevisionHistoryLimit() {
        return revisionHistoryLimit = spec.getRevisionHistoryLimit();
    }

    public Object getSelector() {
        return selector = spec.getSelector();
    }

    public int getUpdated() {
        return updated = status.getUpdatedReplicas();
    }

    public int getTotal() {
        return total = status.getReplicas();
    }

    public int getAvailable() {
        return available = status.getAvailableReplicas();
    }
}
