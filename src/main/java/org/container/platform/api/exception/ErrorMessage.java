package org.container.platform.api.exception;

import lombok.Data;

/**
 * Error Message Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.24
 **/
@Data
public class ErrorMessage {
    private String resultCode;
    private String resultMessage;

    // REST API 호출 시 에러
    private int httpStatusCode;
    private String detailMessage;

    public ErrorMessage(int httpStatusCode, String detailMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultMessage = detailMessage;
    }

    public ErrorMessage(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public ErrorMessage(String resultCode, String resultMessage, int httpStatusCode, String detailMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.httpStatusCode = httpStatusCode;
        this.detailMessage = detailMessage;
    }
}
