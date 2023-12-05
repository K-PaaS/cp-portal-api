package org.container.platform.api.workloads.replicaSets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReplicaSets List Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.23
 */
@Data
public class ReplicaSetsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<ReplicaSetsListItem> items;

}

@Data
class ReplicaSetsListItem {
    private String name;
    private String namespace;
    private int runningPods;
    private int totalPods;
    private List<String> images;
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

    public Object getImages() {
        List<String> images = new ArrayList<>();
        List<CommonContainer> containers = new ArrayList<CommonContainer>();
        try {
            containers = spec.getTemplate().getSpec().getContainers();
            for (CommonContainer c : containers) {
                images.add(CommonUtils.procReplaceNullValue(c.getImage()));
            }
        } catch (Exception e) {
            return CommonUtils.procReplaceNullValue(images);
        }
        return CommonUtils.procReplaceNullValue(images);
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
