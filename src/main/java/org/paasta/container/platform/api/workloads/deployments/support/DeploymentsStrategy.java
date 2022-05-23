package org.paasta.container.platform.api.workloads.deployments.support;

import lombok.Data;

/**
 * Deployments Strategy Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class DeploymentsStrategy {
    private String type;
    private RollingUpdateDeployments rollingUpdate;
}
