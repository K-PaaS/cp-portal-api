package org.paasta.container.platform.api.workloads.pods.support;

import lombok.Data;

import java.util.Map;

/**
 * Container Statuses Item Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class ContainerStatusesItem {
    private String name;
    private Map<String, Object> state;
    private String image;
    private Integer restartCount;
}
