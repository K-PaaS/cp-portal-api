package org.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Pod Template Spec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonPodTemplateSpec {
    private CommonMetaData metadata;
    private CommonPodSpec spec;
}
