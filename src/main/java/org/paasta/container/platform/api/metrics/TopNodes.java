package org.paasta.container.platform.api.metrics;

import lombok.Data;

@Data
public class TopNodes {
    private String clusterName;
    private String clusterId;
    private String name;
    private String percentage;
    private String usage;

    public TopNodes(String clusterName, String clusterId, String name, String percentage, String usage) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.name = name;
        this.percentage = percentage;
        this.usage = usage;
    }
}
