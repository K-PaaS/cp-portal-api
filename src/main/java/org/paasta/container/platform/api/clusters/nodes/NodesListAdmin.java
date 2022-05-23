package org.paasta.container.platform.api.clusters.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonCondition;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * Nodes List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.01
 */
@Data
public class NodesListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<NodesListAdminItem> items;
}

@Data
class NodesListAdminItem {
    private String name;
    private Object labels;
    private String ready;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return name = metadata.getName();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getReady() {
        List<CommonCondition> conditions = status.getConditions();
        for (CommonCondition c : conditions) {
            if (c.getType().equals(Constants.STRING_CONDITION_READY)) {
                ready = c.getStatus();
            }
        }

        return ready;
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }
}
