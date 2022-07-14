package org.paasta.container.platform.api.metrics;

import lombok.Data;
import java.util.Map;

@Data
public class TopNodes {
    private String clusterName;
    private String clusterId;
    private String name;
    private Map<String, String> cpu;
    private Map<String, String> memory;

    public TopNodes(String clusterName, String clusterId, String name, Map<String, String> cpu, Map<String, String> memory) {
        this.clusterName = clusterName;
        this.clusterId = clusterId;
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
    }
}
