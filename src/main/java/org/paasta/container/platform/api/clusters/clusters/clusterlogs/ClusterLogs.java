package org.paasta.container.platform.api.clusters.clusters.clusterlogs;

import lombok.Data;

@Data
public class ClusterLogs {
    private String clusterId;
    private int processNo;
    private String logMessage;
    private String regTimestamp;
}
