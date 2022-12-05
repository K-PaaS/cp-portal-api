package org.paasta.container.platform.api.workloads.deployments.support;

import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;

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

    public String getMaxSurge() {
        return CommonUtils.procReplaceNullValue(maxSurge);
    }

    public String getMaxUnavailable() {
        return CommonUtils.procReplaceNullValue(maxUnavailable);
    }
}
