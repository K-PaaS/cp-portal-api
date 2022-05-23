package org.paasta.container.platform.api.common;

import org.paasta.container.platform.api.login.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Message Constants 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.06
 */
public enum MessageConstant {
    NOT_ALLOWED_POD_NAME("부적절한 Pod 이름 입니다.","Improper pod name."),
    NOT_ALLOWED_RESOURCE_NAME("부적절한 리소스 이름 입니다.","Improper resource name."),
    PREFIX_KUBE_NOT_ALLOW("'kube-' 접두사는 허용되지 않습니다.","'kube-' prefixes are not allowed."),
    NOT_MATCH_NAMESPACES("현재 네임스페이스와 요청한 네임스페이스가 일치하지 않습니다.","The current namespace and the requested namespace do not match."),
    NOT_MATCH_USER_ID("현재 User ID와 요청한 User ID가 일치하지 않습니다.","The current User ID and the requested User ID do not match."),
    NOT_EXIST_RESOURCE("해당 리소스가 존재하지 않습니다.","The resource does not exist."),
    NOT_EXIST(" 리소스 kind 가 존재하지 않습니다."," resource kind does not exist."),
    NOT_UPDATE_YAML(" 에 대한 수정 yaml 형식이 아닙니다."," is not in yaml format for edit."),
    RESOURCE_NAMED(" 이름을 가진 리소스"," resources with names"),
    INCLUDE_INACCESSIBLE_RESOURCES("User 권한으로 생성이 불가한 리소스가 포함되어있습니다.","Resources that cannot be created with User permissions are included."),
    INVALID_YAML_FORMAT("잘못된 YAML 형식입니다.","Invalid YAML format."),
    DO_NOT_DELETE_DEFAULT_RESOURCES("해당 리소스는 삭제할 수 없습니다.","You cannot delete that resource."),
    MANDATORY_NAMESPACE_AND_ROLE("Namespace와 Role 선택은 필수입니다.","Namespace and Role selection is mandatory."),
    UNAPPROACHABLE_USERS("지정이 불가한 사용자입니다.","Users cannot be assigned."),
    REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT("Namespace 관리자 지정이 필요합니다.","Namespace administrator assignment is required."),
    NAMESPACES_CANNOT_BE_CREATED("리소스 생성이 불가한 Namespace 입니다.","Resource generation in this Namespace is not possible."),
    NAMESPACES_CANNOT_BE_MODIFIED("리소스 수정이 불가한 Namespace 입니다.","Resource editing in this Namespace is not possible."),
    CODE_ERROR("요청 사항에 오류가 발생하였습니다. 관리자에게 문의하세요.","The request has failed. Contact your administrator."),
    REQUEST_VALUE_IS_MISSING("필수사항이 누락되었습니다.","Requirements are missing."),
    NON_EXISTENT_ID("존재하지 않는 사용자 아이디입니다.","Username does not exist."),
    UNAVAILABLE_ID("해당 사용자 아이디는 사용할 수 없습니다.","This username cannot be used."),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다.","The password is incorrect."),
    ID_REQUIRED("사용자 아이디를 입력해주세요.","Please enter your username."),
    PASSWORD_REQUIRED("비밀번호를 입력해주세요.","Please enter your password."),
    AUTH_ID_REQUIRED("사용자 인증 아이디를 입력해주세요.","Please enter your verification ID"),
    ID_PASSWORD_REQUIRED("사용자 아이디와 비밀번호를 입력해주세요.","Please enter your username and password."),
    INACTIVE_USER_ACCESS("승인되지 않은 사용자입니다. 관리자에게 문의하시기 바랍니다.","Unauthorized user. Please contact your administrator."),
    INVALID_LOGIN_INFO("로그인 정보가 올바르지 않습니다.","The login information is incorrect."),
    USER_SIGN_UP_INFO_REQUIRED("사용자의 아이디 또는 인증 아이디가 올바르지 않습니다.","The user's ID or verification ID is incorrect."),
    LIMIT_ILLEGALARGUMENT("limit(한 페이지에 가져올 리소스 최대 수) 는 반드시 0 이상이여아 합니다. limit >=0","Limit (maximum number of resources to import on a page) must be at least zero. limit >=0"),
    OFFSET_ILLEGALARGUMENT("offset(목록 시작지점) 은 반드시 0 이상이여아 합니다. offset >=0","Offset must be at least zero. offset >=0"),
    OFFSET_REQUIRES_LIMIT_ILLEGALARGUMENT("offset(목록 시작지점) 사용 시 limit(한 페이지에 가져올 리소스 최대 수) 값이 필요합니다.","When using offset, a limit value is required."),
    USER_TYPE_ILLEGALARGUMENT("사용자 유형 선택 목록에 없는 항목입니다.","Items that are not in the Select a user type list."),
    DUPLICATE_USER_ID("User ID가 중복입니다.","User ID is duplicated."),
    RE_CONFIRM_INPUT_VALUE("입력 값을 다시 확인해 주세요.","Please check the input value again."),
    REGISTER_FAIL("회원가입에 실패했습니다.","Member registration failed."),
    NOT_UPDATE_YAML_FORMAT_THIS_RESOURCE("리소스 명이 올바르지 않습니다.", "The resource name is not valid."),
    LOGIN_SUCCESS("Login Successful.","Login Successful."),
    LOGIN_FAIL("LOGIN_FAILED","LOGIN_FAILED"),
    REFRESH_TOKEN_SUCCESS("Refresh Token Successful.","Refresh Token Successful."),
    REFRESH_TOKEN_FAIL("Refresh Token Failed.","Refresh Token Failed."),
    LOGIN_TOKEN_FAIL("TOKEN_FAILED","TOKEN_FAILED"),
    LOGIN_TOKEN_EXPIRED("TOKEN_EXPIRED","TOKEN_EXPIRED"),
    LOGIN_INVALID_CREDENTIALS("INVALID_CREDENTIALS","INVALID_CREDENTIALS"),
    LOGIN_TOKEN_FAIL_MESSAGE("Token authentication failed","Token authentication failed"),
    LOGIN_TOKEN_EXPIRED_MESSAGE("Access Token has Expired","Access Token has Expired"),
    LOGIN_INVALID_CREDENTIALS_MESSAGE("Invalid Credentials","Invalid Credentials"),
    LOGIN_INACTIVE_USER("INACTIVE_USER","INACTIVE_USER"),
    SIGNUP_USER_CREATION_FAILED("SIGNUP_USER_CREATION_FAILED","SIGNUP_USER_CREATION_FAILED"),
    USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE("USER_NOT_REGISTERED_IN_KEYCLOAK","USER_NOT_REGISTERED_IN_KEYCLOAK"),
    USERS_REGISTERED_CHECK_FAIL_MESSAGE( "USERS_REGISTERED_CHECK_FAIL","USERS_REGISTERED_CHECK_FAIL"),
    USER_NOT_MAPPED_TO_THE_NAMESPACE_MESSAGE("USER_NOT_MAPPED_TO_THE_NAMESPACE","USER_NOT_MAPPED_TO_THE_NAMESPACE"),
    INVALID_SERVICE_INSTANCE_ID("INVALID_SERVICE_INSTANCE_ID","INVALID_SERVICE_INSTANCE_ID"),
    CLUSTER_ADMINISTRATOR_IS_ALREADY_REGISTERED_MESSAGE("CLUSTER_ADMIN_ALREADY_REGISTERED","CLUSTER_ADMIN_ALREADY_REGISTERED"),
    USER_ALREADY_REGISTERED_MESSAGE("USER_ALREADY_REGISTERED","USER_ALREADY_REGISTERED");

    private String ko_msg;
    private String eng_msg;

    MessageConstant(String ko_msg, String eng_msg) {
        this.ko_msg = ko_msg;
        this.eng_msg = eng_msg;
    }

    public String getKo_msg() {
        return ko_msg;
    }

    public String getEng_msg() {
        return eng_msg;
    }

    public String getMsg() {
        CustomUserDetails customUserDetails = null;
        String u_lang = "";
        try {
            customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            u_lang = customUserDetails.getULang();
        } catch (Exception e) {
            return getEng_msg();
        }
        if (u_lang.equals(Constants.U_LANG_KO)) {
            return getKo_msg();
        }
        return getEng_msg();
    }


}
