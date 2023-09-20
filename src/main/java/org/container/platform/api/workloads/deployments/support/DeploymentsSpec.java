package org.container.platform.api.workloads.deployments.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonLabelSelector;
import org.container.platform.api.common.model.CommonPodTemplateSpec;

/**
 * Deployments Spec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class DeploymentsSpec {
    private int minReadySeconds;
    private boolean paused;
    private int progressDeadlineSeconds;
    private int replicas;
    private int revisionHistoryLimit;
    private CommonLabelSelector selector;
    private DeploymentsStrategy strategy;
    private CommonPodTemplateSpec template;

}
