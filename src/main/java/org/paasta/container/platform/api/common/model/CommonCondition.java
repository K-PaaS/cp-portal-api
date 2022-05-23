package org.paasta.container.platform.api.common.model;

import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;

/**
 * Common Condition Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonCondition {
    private String type;
    private String status;
    private String message;
    private String reason;
    private String lastHeartbeatTime;
    private String lastTransitionTime;

    public String getLastHeartbeatTime() {
        return CommonUtils.procSetTimestamp(lastHeartbeatTime);
    }

    public String getLastTransitionTime() {
        return CommonUtils.procSetTimestamp(lastTransitionTime);
    }
}
