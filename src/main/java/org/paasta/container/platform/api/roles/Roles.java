package org.paasta.container.platform.api.roles;


import java.util.ArrayList;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;


/**
 * Roles Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class Roles {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private ArrayList rules;

    private String sourceTypeYaml;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
