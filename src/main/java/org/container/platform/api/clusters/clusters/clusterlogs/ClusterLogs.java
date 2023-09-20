package org.container.platform.api.clusters.clusters.clusterlogs;

import lombok.Data;
import org.container.platform.api.common.Constants;

@Data
public class ClusterLogs {
    private String resultCode = Constants.RESULT_STATUS_FAIL;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String clusterId;
    private int processNo;
    private String logMessage;
    private String regTimestamp;
}
