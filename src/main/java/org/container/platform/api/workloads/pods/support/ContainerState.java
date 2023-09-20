package org.container.platform.api.workloads.pods.support;

import lombok.Data;

import java.util.Map;

/**
 * ContainerState Item Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.07.21
 */
@Data
public class ContainerState {

    private String startedAt;
    private String containerID;
    private Integer exitCode;
    private String finishedAt;
    private String message;
    private String reason;
    private Integer signal;


}
