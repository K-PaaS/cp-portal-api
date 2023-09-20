package org.container.platform.api.exception;

/**
 * Base Biz Exception Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.24
 **/
public class BaseBizException extends RuntimeException {

	private static final long serialVersionUID = 1032826776466587212L;

    private String errorCode;
    private String errorMessage;
    private int statusCode;
    private String detailMessage;

    public BaseBizException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public BaseBizException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    };

    public BaseBizException(String errorCode, String errorMessage, int statusCode, String detailMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.detailMessage = detailMessage;
    };

    public BaseBizException(String errorCode, int statusCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    };
    
    public BaseBizException(String errorMessage, String errorCode, Throwable cause) {
    	super(errorMessage, cause);
    	this.errorMessage = errorMessage;
    	this.errorCode = errorCode;
    };

    public BaseBizException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    public BaseBizException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode(){
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

}
