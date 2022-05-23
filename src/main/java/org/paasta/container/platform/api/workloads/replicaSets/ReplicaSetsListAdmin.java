package org.paasta.container.platform.api.workloads.replicaSets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * ReplicaSets List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.09.10
 */
@Data
public class ReplicaSetsListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<ReplicaSetsListAdminItem> items;

}

@Data
class ReplicaSetsListAdminItem {
    private String name;
    private String namespace;
    private int runningPods;
    private int totalPods;
    private String image;
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

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public int getRunningPods() {
        return status.getAvailableReplicas();
    }

    public int getTotalPods() {
        return status.getReplicas();
    }

    public String getImage() {
        return spec.getTemplate().getSpec().getContainers().get(0).getImage();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
