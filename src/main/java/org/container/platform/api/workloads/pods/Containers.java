package org.container.platform.api.workloads.pods;

import lombok.Data;

/**
 * Containers Model 클래스
 *
 * @author Lee
 * @version 1.0
 * @since 2020.11.20
 */
@Data
public class Containers {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String name;
    private ContainerUsage usage;
}
