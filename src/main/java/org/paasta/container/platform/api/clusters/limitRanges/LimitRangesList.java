package org.paasta.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;

import java.util.List;

/**
 * LimitRanges List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Data
public class LimitRangesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    @JsonIgnore
    private CommonItemMetaData itemMetaData;
    private List<LimitRanges> items;
    private List<LimitRangesListItem> limits;

class LimitRangesListItem {
    private List<LimitRangesItem> limits;
    @JsonIgnore
    private CommonSpec spec;

    public List<LimitRangesItem> getLimits() {
        return spec.getLimits();
    }

    public void setLimits(List<LimitRangesItem> limits) {
        this.limits = limits;
    }
}


}
