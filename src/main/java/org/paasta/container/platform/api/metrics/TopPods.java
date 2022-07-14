package org.paasta.container.platform.api.metrics;

import lombok.Data;
import java.util.Map;

@Data
public class TopPods {
    private String clusterName;
    private String clusterId;
    private String namespace;
    private String name;
    private Map<String, String> cpu;
    private Map<String, String> memory;

    public TopPods(String clusterName, String clusterId, String namespace, String name, Map<String, String> cpu, Map<String, String> memory) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.namespace = namespace;
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
    }
}
