package org.paasta.container.platform.api.metrics;

import lombok.Data;

@Data
public class TopPods {
    private String clusterName;
    private String clusterId;
    private String namespace;
    private String name;
    private String usage;

    public TopPods(String clusterName, String clusterId, String namespace, String name, String usage) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.namespace = namespace;
        this.name = name;
        this.usage = usage;
    }
}
