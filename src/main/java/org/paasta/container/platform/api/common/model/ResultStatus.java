package org.paasta.container.platform.api.common.model;

import lombok.Builder;
import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;

/**
 * Result Status model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.28
 **/
@Data
@Builder
public class ResultStatus {
    private String resultCode;
    private String resultMessage;
    private int httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    public ResultStatus() {
    }

    public ResultStatus(String resultCode, String resultMessage, int httpStatusCode, String detailMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
    }

    public ResultStatus(String resultCode, String resultMessage, int httpStatusCode, String detailMessage, String nextActionUrl) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
        this.nextActionUrl = nextActionUrl;
    }

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
