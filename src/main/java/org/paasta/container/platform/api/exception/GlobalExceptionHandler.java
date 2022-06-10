package org.paasta.container.platform.api.exception;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Iterator;

/**
 * GlobalException Handler 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 **/
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    ResultStatusService resultStatusService;

    public GlobalExceptionHandler() {

    }

    @ExceptionHandler({HttpClientErrorException.class})
    @ResponseBody
    public ErrorMessage handleException(HttpClientErrorException ex) {
        LOGGER.info("HttpClientErrorException >>> " + CommonUtils.loggerReplace(ex.getStatusText()));
        for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
            if (code.getCode() == ex.getRawStatusCode()) {
                return new ErrorMessage(Constants.RESULT_STATUS_FAIL, code.getMsg(), code.getCode(), code.getMsg());
            }
        }

        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, ex.getStatusText(), ex.getRawStatusCode(), ex.getResponseBodyAsString());
    }

    @ExceptionHandler({ContainerPlatformException.class})
    @ResponseBody
    public ErrorMessage handleException(ContainerPlatformException ex) {
        LOGGER.info("ContainerPlatformException >>> " + CommonUtils.loggerReplace(ex.getErrorMessage()));
        return new ErrorMessage(ex.getErrorCode(), ex.getErrorMessage(), ex.getStatusCode(), ex.getDetailMessage());
    }

    @ExceptionHandler({CpCommonAPIException.class})
    @ResponseBody
    public ErrorMessage handleException(CpCommonAPIException ex) {
        LOGGER.info("CpCommonAPIException >>> " + CommonUtils.loggerReplace(ex.getErrorMessage()));
        return new ErrorMessage(ex.getErrorCode(), ex.getErrorMessage(), ex.getStatusCode(), ex.getDetailMessage());
    }


    @ExceptionHandler({Exception.class})
    public ErrorMessage handleAll(final Exception ex) {
        if (ex.getMessage().contains("404")) {
            return new ErrorMessage(Constants.RESULT_STATUS_FAIL, CommonStatusCode.NOT_FOUND.getMsg(), HttpStatus.NOT_FOUND.value(), CommonStatusCode.NOT_FOUND.getMsg());
        }

        LOGGER.info("Exception >>> {}", CommonUtils.loggerReplace(ex.getLocalizedMessage()));
        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg(), HttpStatus.INTERNAL_SERVER_ERROR.value(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ErrorMessage handleException(HttpMessageNotReadableException ex) {

        String message = "Required request body is missing";
        if (ex.getMessage().contains(message)) {
            return new ErrorMessage(Constants.RESULT_STATUS_FAIL, MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg(), HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
        }
        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getLocalizedMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ErrorMessage handleException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        String message = MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg() + " : ";

        FieldError error;
        for (Iterator var5 = result.getFieldErrors().iterator(); var5.hasNext(); message = message + error.getField()) {
            error = (FieldError) var5.next();
        }
        LOGGER.info("MethodArgumentNotValidException >>> " + CommonUtils.loggerReplace(message));

        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, message);
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ErrorMessage nullException(NullPointerException ex) {
        LOGGER.info("NullPointerException >>> " + CommonUtils.loggerReplace(ex));
        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, MessageConstant.CODE_ERROR.getMsg());
    }

    @ExceptionHandler({IndexOutOfBoundsException.class})
    @ResponseBody
    public ErrorMessage indexOutOfBoundsException(IndexOutOfBoundsException ex) {
        LOGGER.info("indexOutOfBoundsException >>> " + CommonUtils.loggerReplace(ex.getMessage()));
        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, MessageConstant.CODE_ERROR.getMsg());
    }

    @ExceptionHandler({ClassCastException.class})
    @ResponseBody
    public ErrorMessage classCastException(ClassCastException ex) {
        LOGGER.info("ClassCastException >>> " + CommonUtils.loggerReplace(ex.getMessage()));
        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, MessageConstant.CODE_ERROR.getMsg());
    }

    @ExceptionHandler({CommonStatusCodeException.class})
    @ResponseBody
    public ResultStatus commonStatusCodeException(CommonStatusCodeException ex) {

        for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
            if (ex.getMessage().contains(Integer.toString(code.getCode()))) {
                return new ResultStatus(Constants.RESULT_STATUS_FAIL, code.getMsg(),
                        code.getCode(), code.getMsg());
            }
        }

        return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg(),
                CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
    }


    @ExceptionHandler({ResultStatusException.class})
    @ResponseBody
    public ResultStatus resultStatusException(ResultStatusException ex) {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, ex.getMessage(), CommonStatusCode.OK.getCode(), ex.getMessage());
    }

}