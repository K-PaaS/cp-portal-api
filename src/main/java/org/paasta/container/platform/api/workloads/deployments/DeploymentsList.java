package org.paasta.container.platform.api.workloads.deployments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsSpec;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsStatus;

import java.util.List;

/**
 * Deployments List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.08
 */
@Data
public class DeploymentsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<DeploymentsListItem> items;
}

@Data
class DeploymentsListItem {
    private String name;
    private String namespace;
    private int runningPods;
    private int totalPods;
    private String images;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private DeploymentsSpec spec;

    @JsonIgnore
    private DeploymentsStatus status;

    public String getName() {
        return name = metadata.getName();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }

    public int getRunningPods() {
        return runningPods = status.getAvailableReplicas();
    }

    public int getTotalPods() {
        return totalPods = status.getReplicas();
    }

    public String getImages() {
        return images = spec.getTemplate().getSpec().getContainers().get(0).getImage();
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }
}
