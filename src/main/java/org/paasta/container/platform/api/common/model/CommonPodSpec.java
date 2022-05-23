package org.paasta.container.platform.api.common.model;

import lombok.Data;

import java.util.List;

/**
 * Common Pod Spec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonPodSpec {
    private List<CommonContainer> containers;
}
