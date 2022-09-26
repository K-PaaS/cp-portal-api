package org.paasta.container.platform.api.signUp;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.clusters.ClustersService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.ResultStatusException;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.Map;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Sign Up Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.07
 **/
@Service
public class SignUpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpService.class);

    private final PropertyService propertyService;
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final AccessTokenService accessTokenService;
    private final UsersService usersService;
    private final ResourceYamlService resourceYamlService;
    private final ClustersService clustersService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SignUpService
     * @param propertyService the property service
     * @param restTemplateService the rest template service
     * @param commonService the common service
     * @param accessTokenService the access token service
     * @param usersService the users service
     * @param resourceYamlService the resource yaml service
     * @param clustersService the clusters service
     */
    @Autowired
    public SignUpService(PropertyService propertyService, RestTemplateService restTemplateService, CommonService commonService, AccessTokenService accessTokenService,
                         UsersService usersService, ResourceYamlService resourceYamlService, ClustersService clustersService, ResultStatusService resultStatusService) {
        this.propertyService = propertyService;
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.accessTokenService = accessTokenService;
        this.usersService = usersService;
        this.resourceYamlService = resourceYamlService;
        this.clustersService = clustersService;
        this.resultStatusService = resultStatusService;
    }


    /**
     *
     *  Users 회원가입(Sign Up Users)
     *
     * @param params
     * @return the resultStatus
     */
    public ResultStatus signUpUsers(Params params) {
        // 1-1. 해당 계정이 KEYCLOAK에 등록된 사용자인지 확인
        // 1-2. TYPE : SUPER-ADMIN, SUPER-ADMIN 권한이 이미 등록된 경우
        // 1-3. TYPE: USER, 해당 USER가 이미 등록된 경우
        // 메세지 리턴 처리
        UsersList usersList = checkUserSignUp(params);

        // 2. KEYCLOAK 내 삭제된 동일한 이전 USER-ID의 K8S SA, ROLEBINDING 삭제 진행
        resourceYamlService.deleteUserResourceForNonExistentUser(usersList);

        // 3. CP-USER 계정 생성
        Users newUser = new Users(NULL_REPLACE_TEXT, propertyService.getDefaultNamespace(), params.getUserId(), params.getUserAuthId(),
                params.getUserType(), NULL_REPLACE_TEXT, params.getUserAuthId(), NULL_REPLACE_TEXT, NULL_REPLACE_TEXT);

        // 6. 계정생성 COMMON-API REST SEND
        ResultStatus rsDb = sendSignUpUser(newUser);

        if(Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
            LOGGER.info("[CREATEING USER] DATABASE EXECUTE IS FAILED....");
            throw new ResultStatusException(MessageConstant.SIGNUP_USER_CREATION_FAILED.getMsg());
        }

        return (ResultStatus) commonService.setResultModel(rsDb, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Super Admin(시스템 관리자) 등록여부 조회(Check Super Admin Registration)
     *
     * @return the users
     */
    public UsersList checkUserSignUp(Params params) {
        // 클러스터 관리자 등록 여부 조회
        Object result= restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CHECK_USER_REGISTER
                        .replace("{userId:.+}", params.getUserId())
                        .replace("{userAuthId:.+}", params.getUserAuthId())
                        .replace("{userType:.+}", params.getUserType())
                , HttpMethod.GET, null,  Map.class, new Params());

        if(result instanceof ResultStatus) {
            LOGGER.info("FAILD USER SIGN UP : {}", CommonUtils.loggerReplace(((ResultStatus) result).getResultMessage()));
            throw new ResultStatusException(((ResultStatus) result).getResultMessage());
        }

        UsersList usersList = commonService.setResultObject(result, UsersList.class);
        return usersList;
    }


    /**
     * 클러스터 관리자 회원가입 (Send Cluster Admin Registration)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus sendSignUpUser(Users users) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_SIGNUP, HttpMethod.POST, users, ResultStatus.class, new Params());
    }

}
