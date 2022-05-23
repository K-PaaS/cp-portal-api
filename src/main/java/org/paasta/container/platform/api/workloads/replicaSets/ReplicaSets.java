package org.paasta.container.platform.api.workloads.replicaSets;

import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * ReplicaSets Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.09.10
 */
@Data
public class ReplicaSets {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private CommonSpec spec;
    private CommonStatus status;

    private Map<String, Object> source;
    private String sourceTypeYaml;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
