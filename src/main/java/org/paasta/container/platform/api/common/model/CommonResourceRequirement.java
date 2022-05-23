package org.paasta.container.platform.api.common.model;

import lombok.Data;

import java.util.Map;

/**
 * Common Resource Requirement Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Data
public class CommonResourceRequirement {
    private Map<String, Object> limits;
    private Map<String, Object> requests;
    private Map<String, Object> usage;
}
