package org.container.platform.api.workloads.pods;

import lombok.Data;

/**
 * Container Usage Model 클래스
 *
 * @author Lee
 * @version 1.0
 * @since 2020.11.20
 */
@Data
public class ContainerUsage {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String cpu;
    private String memory;
}
