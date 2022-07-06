package org.paasta.container.platform.api.clusters.nodes.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonCondition;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonStatus;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


@Data
public class NodesListItem {
    private String name;
    private Object labels;
    private String ready;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonStatus status;

    @Autowired
    private CommonService commonService;

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
