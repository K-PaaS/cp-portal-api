package org.container.platform.api.workloads.replicaSets.support;

import lombok.Data;

import java.util.List;

/**
 * ReplicaSets Spec Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.28
 */
@Data
public class ReplicaSetsSpec {
    private List<ReplicaSetsContainer> containers;
}
