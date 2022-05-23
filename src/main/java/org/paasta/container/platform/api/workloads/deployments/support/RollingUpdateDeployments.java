package org.paasta.container.platform.api.workloads.deployments.support;

import lombok.Data;

/**
 * Rolling Update Deployments Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class RollingUpdateDeployments {
    private String maxSurge;
    private String maxUnavailable;
}
