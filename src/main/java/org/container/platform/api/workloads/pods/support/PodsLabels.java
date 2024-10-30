package org.container.platform.api.workloads.pods.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * PodsLabels 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-10-30
 */
@Data
public class PodsLabels {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PodsLabelsItem> items;
}

