package org.container.platform.api.clusters.clusters.clusterlogs;

import lombok.Data;
import org.container.platform.api.common.Constants;

import java.util.List;

@Data
public class ClusterLogsList {
    private String resultCode = Constants.RESULT_STATUS_FAIL;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage = "Log message is empty.";
    private List<ClusterLogs> items;
}
