package org.container.platform.api.workloads.pods;

import lombok.Data;
import org.container.platform.api.common.model.CommonMetaData;

import java.util.List;

/**
 * Pods Usage Model 클래스
 *
 * @author Lee
 * @version 1.0
 * @since 2020.11.20
 */
@Data
public class PodsUsage {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonMetaData metadata;
    private List<Containers> containers;
}
