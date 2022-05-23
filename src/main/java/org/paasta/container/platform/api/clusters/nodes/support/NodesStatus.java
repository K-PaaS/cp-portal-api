package org.paasta.container.platform.api.clusters.nodes.support;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonCondition;

import java.util.List;
import java.util.Map;

/**
 * Nodes Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.31
 */
@Data
public class NodesStatus {
    private Map<String, Object> capacity;
    private Map<String, Object> allocatable;
    private List<CommonCondition> conditions;
    private List<NodesAddress> addresses;
    private NodesSystemInfo nodeInfo;
}
