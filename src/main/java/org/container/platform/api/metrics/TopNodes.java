package org.container.platform.api.metrics;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class TopNodes {
    private String clusterName;
    private String clusterId;
    private String name;
    private Map<String, Object> cpu;
    private Map<String, Object> memory;

    public TopNodes(String clusterName, String clusterId, String name, Map<String, Object> cpu, Map<String, Object> memory) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
    }
}
