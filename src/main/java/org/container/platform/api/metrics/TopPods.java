package org.container.platform.api.metrics;

import lombok.Data;
import java.util.Map;

@Data
public class TopPods {
    private String namespace;
    private String name;
    private Map<String, Object> cpu;
    private Map<String, Object> memory;

    public TopPods(String namespace, String name, Map<String, Object> cpu, Map<String, Object> memory) {
        this.namespace = namespace;
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
    }
}
