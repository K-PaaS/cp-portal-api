package org.paasta.container.platform.api.metrics;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import java.util.List;


@Data
public class MetricsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<MetricsItems> items;
}
