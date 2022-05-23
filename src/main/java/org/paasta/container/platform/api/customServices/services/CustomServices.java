package org.paasta.container.platform.api.customServices.services;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * CustomServices Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.10
 */
@Data
public class CustomServices {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private CommonSpec spec;
    private CommonStatus status;
    private String sourceTypeYaml;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
