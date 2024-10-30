package org.container.platform.api.workloads.pods.support;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PodsLabelsItem 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-10-30
 */
@Data
public class PodsLabelsItem {
    private Map<String, Set<Object>> labels;

    public PodsLabelsItem(Map<String, Set<Object>> labels) {
        this.labels = labels;
    }
}
