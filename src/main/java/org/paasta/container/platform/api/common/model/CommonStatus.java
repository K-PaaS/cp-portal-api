package org.paasta.container.platform.api.common.model;

import lombok.Data;
import org.paasta.container.platform.api.metrics.custom.Quantity;

import java.util.List;
import java.util.Map;

/**
 * Common Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Data
public class CommonStatus {
    private int availableReplicas;
    private int fullyLabeledReplicas;
    private long observedGeneration;
    private int readyReplicas;
    private int replicas;
    private String phase;
    private List<ContainerStatus> containerStatuses;
    private List<CommonCondition> conditions;
    private String podIP;
    private String qosClass;
    private CommonNodeInfo nodeInfo;
    private Map<String, Quantity> capacity;
    private Map<String, Quantity> allocatable;
}
