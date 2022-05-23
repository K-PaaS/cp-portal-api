package org.paasta.container.platform.api.common.model;

import lombok.Data;

import java.util.List;

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
}
