package org.paasta.container.platform.api.configmaps;

import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;

import java.util.Map;

@Data
public class ConfigMaps {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private boolean immutable;
    private Map<String, String> data;
    private Map<String, Byte[]> binaryData;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
