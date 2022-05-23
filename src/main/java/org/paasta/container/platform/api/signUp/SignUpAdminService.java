package org.paasta.container.platform.api.signUp;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.adminToken.AdminTokenService;
import org.paasta.container.platform.api.clusters.clusters.ClustersService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersListAdmin;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Sign Up Admin Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Service
public class SignUpAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpAdminService.class);

    private final PropertyService propertyService;
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final AccessTokenService accessTokenService;
    private final UsersService usersService;
    private final ResourceYamlService resourceYamlService;
    private final AdminTokenService adminTokenService;
    private final ClustersService clustersService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SignUpAdminService service
     * @param propertyService the property service
     * @param restTemplateService the rest template service
     * @param commonService the common service
     * @param accessTokenService the access token service
     * @param usersService the users service
     * @param resourceYamlService the resource yaml service
     * @param adminTokenService the admin token service
     * @param clustersService the clusters service
     */
    @Autowired
    public SignUpAdminService(PropertyService propertyService, RestTemplateService restTemplateService, CommonService commonService, AccessTokenService accessTokenService,
                              UsersService usersService, ResourceYamlService resourceYamlService, AdminTokenService adminTokenService, ClustersService clustersService, ResultStatusService resultStatusService) {
        this.propertyService = propertyService;
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.accessTokenService = accessTokenService;
        this.usersService = usersService;
        this.resourceYamlService = resourceYamlService;
        this.adminTokenService = adminTokenService;
        this.clustersService = clustersService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * 운영자 회원가입(Sign Up Admin)
     *
     * @param users the user
     * @return the resultStatus
     */
    public ResultStatus signUpAdminUsers(Users users, String param) {

        // 1. 해당 계정이 KEYCLOAK에 등록된 사용자인지 확인
        // CP USER에 클러스터 관리자 계정이 이미 등록되어있는지 확인
        UsersListAdmin registerClusterAdmin = checkClusterAdminRegister(users);


        // 2. KEYCLOAK 에 미등록 사용자인 경우, 메세지 리턴 처리
        if(registerClusterAdmin.getResultMessage().equals(MessageConstant.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE.getMsg())) {
            return resultStatusService.USER_NOT_REGISTERED_IN_KEYCLOAK();
        }

        // 3. CP USER에 클러스터 관리자 계정이 이미 등록된 경우, 메세지 리턴 처리
        if(registerClusterAdmin.getItems().size() > 0) {
            return resultStatusService.CLUSTER_ADMINISTRATOR_IS_ALREADY_REGISTERED();
        }


        // save admin token <- release 설치 시 작업  -  save admin token
        // save cluster info <- release 설치 시 작업 - clustersService.createClusters



        //4. KEYCLOAK에서는 삭제된 계정이지만, CP에 남아있는 동일한 USER ID의 DB 컬럼, K8S SA, ROLEBINDING 삭제 진행
        UsersList usersList = getUsersListByUserId(users.getUserId());

        List<Users> deleteUsers = usersList.getItems().stream().filter(x-> !x.getUserAuthId().matches(users.getUserAuthId())).collect(Collectors.toList());

        for(Users du: deleteUsers) {
            usersService.deleteUsers(du);
        }

        // 5. CP-USER에 클러스터 관리자 계정 생성
        users.setCpNamespace(propertyService.getDefaultNamespace());
        users.setServiceAccountName(users.getUserAuthId());
        users.setRoleSetCode(DEFAULT_CLUSTER_ADMIN_ROLE);
        users.setSaSecret(NULL_REPLACE_TEXT);
        users.setSaToken(NULL_REPLACE_TEXT);
        users.setUserType(AUTH_CLUSTER_ADMIN);
        users.setIsActive(CHECK_Y);


        // 6. 계정생성 COMMON-API REST SEND
        ResultStatus rsDb = sendSignUpClusterAdmin(users, param);

        if(Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
            LOGGER.info("DATABASE EXECUTE IS FAILED....");
            return resultStatusService.CREATE_USERS_FAIL();
        }


        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, "/");
    }



    /**
     * 클러스터 관리자 등록여부 조회(Cluster Admin Registration Check)
     *
     * @return the users
     */
    public UsersListAdmin checkClusterAdminRegister(Users users) {

        // 클러스터 관리자 등록 여부 조회
        UsersListAdmin clusterAdmin = restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_CHECK_CLUSTER_ADMIN_REGISTER
                        .replace("{userId:.+}", users.getUserId())
                        .replace("{userAuthId:.+}", users.getUserAuthId())
                , HttpMethod.GET, null, UsersListAdmin.class);

        return clusterAdmin;
    }


    /**
     * 클러스터 관리자 회원가입 (Send Cluster Admin Registration)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus sendSignUpClusterAdmin(Users users, String param) {
        String paramString = "?param=" + param;
        return restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_ADMIN_SIGNUP+ paramString, HttpMethod.POST, users, ResultStatus.class);
    }


    /**
     * 아이디로 존재하는 USER 계정 조회(Get users by user id)
     *
     * @param userId the userId
     * @return the users detail
     */
    public UsersList getUsersListByUserId(String userId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_DETAIL.replace("{userId:.+}", userId), HttpMethod.GET, null, UsersList.class);
    }


}
