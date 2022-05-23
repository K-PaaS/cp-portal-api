package org.paasta.container.platform.api.clusters.nodes;

import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.clusters.nodes.support.NodesStatus;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;

/**
 * Nodes Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.01
 */
@Data
public class Nodes {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private CommonSpec spec;
    private NodesStatus status;

    private Map<String, Object> source;
    private String sourceTypeYaml;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
