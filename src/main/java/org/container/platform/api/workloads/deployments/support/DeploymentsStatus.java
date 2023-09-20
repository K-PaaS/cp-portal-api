package org.container.platform.api.workloads.deployments.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonCondition;

import java.util.List;

/**
 * Deployments Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class DeploymentsStatus {
    private int availableReplicas;
    private int collisionCount;
    private List<CommonCondition> conditions;
    private long observedGeneration;
    private int readyReplicas;
    private int replicas;
    private int unavailableReplicas;
    private int updatedReplicas;
}
