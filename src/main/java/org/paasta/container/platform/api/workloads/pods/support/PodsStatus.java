package org.paasta.container.platform.api.workloads.pods.support;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonCondition;

import java.util.List;

/**
 * Pods status model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.30
 **/
@Data
public class PodsStatus {
    private String phase;
    private List<CommonCondition> conditions;
    private String hostIP;
    private String podIP;
    private List podIPs;
    private String startTime;
    private List<ContainerStatusesItem> containerStatuses;
    private String qosClass;
}
