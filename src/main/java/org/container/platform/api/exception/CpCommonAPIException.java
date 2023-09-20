package org.container.platform.api.exception;

/**
 * Cp Common API Exception Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.24
 **/
public class CpCommonAPIException extends BaseBizException {
	private static final long serialVersionUID = -1288712633779609678L;

	public CpCommonAPIException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public CpCommonAPIException(String errorCode, String errorMessage, int statusCode, String detailMessage) {
		super(errorCode, errorMessage, statusCode, detailMessage);
	}

	public CpCommonAPIException(String errorMessage) {
		super(errorMessage);
	}
}
