package org.container.platform.api.workloads.deployments.support;

import lombok.Data;
import org.container.platform.api.common.CommonUtils;

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


    public String getType() {
        return CommonUtils.procReplaceNullValue(type);
    }

    public RollingUpdateDeployments getRollingUpdate() {
        if (rollingUpdate == null) {
            return new RollingUpdateDeployments();
        }
        return rollingUpdate;
    }
}
