package org.paasta.container.platform.api.common;

import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.stereotype.Service;

@Service
public class ResultStatusService {

    public ResultStatus SUCCESS_RESULT_STATUS() {
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, CommonStatusCode.OK.getMsg(),
                CommonStatusCode.OK.getCode(), CommonStatusCode.OK.getMsg(), null);
    }

    public ResultStatus BAD_REQUEST_ACCESS_RESULT_STATUS() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(), CommonStatusCode.BAD_REQUEST.getMsg(), null);
    }

    public ResultStatus FORBIDDEN_ACCESS_RESULT_STATUS() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.FORBIDDEN.getMsg(),
                CommonStatusCode.FORBIDDEN.getCode(),CommonStatusCode.FORBIDDEN.getMsg());
    }

    public ResultStatus NOT_FOUND_RESULT_STATUS() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.NOT_FOUND.getMsg(),
                CommonStatusCode.NOT_FOUND.getCode(),CommonStatusCode.NOT_FOUND.getMsg());
    }

    public ResultStatus NOT_MATCH_NAMESPACES() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_MATCH_NAMESPACES.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),CommonStatusCode.BAD_REQUEST.getMsg(), null );
    }

    public ResultStatus DO_NOT_DELETE_DEFAULT_RESOURCES() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.DO_NOT_DELETE_DEFAULT_RESOURCES.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.DO_NOT_DELETE_DEFAULT_RESOURCES.getMsg(), null );
    }

    public ResultStatus MANDATORY_NAMESPACE_AND_ROLE() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.MANDATORY_NAMESPACE_AND_ROLE.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.MANDATORY_NAMESPACE_AND_ROLE.getMsg(), null );
    }

    public ResultStatus UNAPPROACHABLE_USERS() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.UNAPPROACHABLE_USERS.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.UNAPPROACHABLE_USERS.getMsg(), null );
    }


    public ResultStatus REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT.getMsg(), null );
    }

    public ResultStatus UNABLE_TO_CREATE_RESOURCE_NAME() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_RESOURCE_NAME.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.NOT_ALLOWED_RESOURCE_NAME.getMsg(), null );
    }

    public ResultStatus REQUEST_VALUE_IS_MISSING() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg(), null );
    }

    // Sign Up
    public ResultStatus CLUSTER_ADMINISTRATOR_IS_ALREADY_REGISTERED() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.CLUSTER_ADMINISTRATOR_IS_ALREADY_REGISTERED_MESSAGE.getMsg(),
                CommonStatusCode.CONFLICT.getCode(),MessageConstant.CLUSTER_ADMINISTRATOR_IS_ALREADY_REGISTERED_MESSAGE.getMsg(), null );
    }

    public ResultStatus USER_ALREADY_REGISTERED() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.USER_ALREADY_REGISTERED_MESSAGE.getMsg(),
                CommonStatusCode.CONFLICT.getCode(),MessageConstant.USER_ALREADY_REGISTERED_MESSAGE.getMsg(), null );
    }

    public ResultStatus USERS_REGISTERED_CHECK_FAIL() {
        return  new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.USERS_REGISTERED_CHECK_FAIL_MESSAGE.getMsg(),
                CommonStatusCode.INTERNAL_SERVER_ERROR.getCode() ,MessageConstant.USERS_REGISTERED_CHECK_FAIL_MESSAGE.getMsg() );
    }

    public ResultStatus CREATE_USERS_FAIL() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.SIGNUP_USER_CREATION_FAILED.getMsg(),
                CommonStatusCode.INTERNAL_SERVER_ERROR.getCode() ,MessageConstant.SIGNUP_USER_CREATION_FAILED.getMsg());
    }


    public ResultStatus USER_NOT_REGISTERED_IN_KEYCLOAK() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE.getMsg(),
                CommonStatusCode.UNAUTHORIZED.getCode(),MessageConstant.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE.getMsg(), null );
    }

    public ResultStatus INVALID_USER_SIGN_UP() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.USER_SIGN_UP_INFO_REQUIRED.getMsg(),
                CommonStatusCode.BAD_REQUEST.getCode(),MessageConstant.USER_SIGN_UP_INFO_REQUIRED.getMsg(), null );
    }

    public ResultStatus INVALID_SERVICE_INSTANCE_ID() {
        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.INVALID_SERVICE_INSTANCE_ID.getMsg(),
                CommonStatusCode.UNAUTHORIZED.getCode(),MessageConstant.INVALID_SERVICE_INSTANCE_ID.getMsg(), null );
    }

}
