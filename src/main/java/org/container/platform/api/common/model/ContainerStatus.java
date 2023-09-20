package org.container.platform.api.common.model;

import lombok.Data;

/**
 * Container Status Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.11.09
 */
@Data
public class ContainerStatus {
    private String name;
    private Object state;
    private Object lastState;
    private String ready;
    private Double restartCount;
    private String image;
    private String imageID;
    private String started;
}
