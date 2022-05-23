package org.paasta.container.platform.api.workloads.pods;

import lombok.Data;

import java.util.List;

/**
 * Pods Metric Model 클래스
 *
 * @author Lee
 * @version 1.0
 * @since 2020.11.20
 */
@Data
public class PodsMetric {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<PodsUsage> items;
    private String kind;

}
