package org.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;
import java.util.List;
import java.util.Map;

/**
 * LimitRanges List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class LimitRangesList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<LimitRangesListItem> items;

    @JsonIgnore
    private List<LimitRanges> item;
}

