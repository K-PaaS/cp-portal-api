package org.paasta.container.platform.api.metrics;

import lombok.Data;
import java.util.Map;

@Data
public class TopPods {
    private String clusterName;
    private String clusterId;
    private String namespace;
    private String name;
    private Map<String, Object> cpu;
    private Map<String, Object> memory;

    public TopPods(String clusterName, String clusterId, String namespace, String name, Map<String, Object> cpu, Map<String, Object> memory) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.namespace = namespace;
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
    }
}
