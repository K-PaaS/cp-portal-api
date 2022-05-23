package org.paasta.container.platform.api.exception;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

/**
 * GlobalController Exception Handler 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 **/
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler({HttpClientErrorException.class})
    @ResponseBody
    public ErrorMessage handleException(HttpClientErrorException ex) {
        LOGGER.info("HttpClientErrorException >>> " + CommonUtils.loggerReplace(ex.getLocalizedMessage()));
        for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
            if(code.getCode() == ex.getRawStatusCode()) {
                return new ErrorMessage(Constants.RESULT_STATUS_FAIL, code.getMsg(), code.getCode(), code.getMsg());
            }
        }

        return new ErrorMessage(Constants.RESULT_STATUS_FAIL, ex.getStatusText(), ex.getRawStatusCode(), ex.getResponseBodyAsString());
    }
}

