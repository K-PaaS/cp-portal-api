package org.paasta.container.platform.api.signUp;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Sign Up User Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Service
public class SignUpUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpUserService.class);

    private final CommonService commonService;
    private final PropertyService propertyService;
    private final RestTemplateService restTemplateService;
    private final AccessTokenService accessTokenService;
    private final UsersService usersService;
    private final ResourceYamlRemoveService resourceYamlService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SignUpUserService service
     *
     * @param commonService the common service
     * @param propertyService the property service
     * @param restTemplateService the rest template service
     * @param accessTokenService the access token service
     * @param usersService the users service
     * @param resourceYamlService the resource yaml service
     */
    @Autowired
    public SignUpUserService(CommonService commonService, PropertyService propertyService, RestTemplateService restTemplateService,
                             AccessTokenService accessTokenService, UsersService usersService, ResourceYamlRemoveService resourceYamlService, ResultStatusService resultStatusService) {
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.restTemplateService = restTemplateService;
        this.accessTokenService = accessTokenService;
        this.usersService = usersService;
        this.resourceYamlService = resourceYamlService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * 단독배포 타입의 사용자 회원가입(Sign Up Users By Provider As StandAlone)
     *
     * @param users the users
     * @return the resultStatus
     */
    public ResultStatus signUpUsersByProviderAsStandAlone(Users users) {

        // 1. 해당 계정이 KEYCLOAK, CP USER 에 등록된 계정인지 확인
        UsersList registerUser = checkRegisterUser(users);

        // 2. KEYCLOAK 에 미등록 사용자인 경우, 메세지 리턴 처리
        if(registerUser.getResultMessage().equals(MessageConstant.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE.getMsg())) {
            return resultStatusService.USER_NOT_REGISTERED_IN_KEYCLOAK();
        }


        // 3. CP USER에 등록된 사용자인 경우, 메세지 리턴 처리
        if(registerUser.getItems().size() > 0) {
            return resultStatusService.USER_ALREADY_REGISTERED();
        }


        //4. KEYCLOAK에서는 삭제된 계정이지만, CP에 남아있는 동일한 USER ID의 DB 컬럼, K8S SA, ROLEBINDING 삭제 진행
        UsersList usersList = getUsersListByUserId(users.getUserId());

        List<Users> deleteUsers = usersList.getItems().stream().filter(x-> !x.getUserAuthId().matches(users.getUserAuthId())).collect(Collectors.toList());

        for(Users du: deleteUsers) {
            usersService.deleteUsers(du);
        }


        // 5. CP-USER에 미등록인 사용자 CP-USER 계정 생성
        users.setCpNamespace(propertyService.getDefaultNamespace());
        users.setServiceAccountName(users.getUserAuthId());
        users.setRoleSetCode(NOT_ASSIGNED_ROLE);
        users.setSaSecret(NULL_REPLACE_TEXT);
        users.setSaToken(NULL_REPLACE_TEXT);
        users.setUserType(AUTH_USER);

        // 6. 계정생성 COMMON-API REST SEND
        ResultStatus rsDb = sendSignUpUser(users);

        if(Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
            LOGGER.info("DATABASE EXECUTE IS FAILED....");
            return resultStatusService.CREATE_USERS_FAIL();
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, "/");
    }


    /**
     * 서비스 타입의 사용자 회원가입(Sign Up Users By Provider As Service)
     *
     * @param users the users
     * @return the resultStatus
     */
    public ResultStatus signUpUsersByProviderAsService(Users users) {

        ServiceInstanceList findServiceInstance = new ServiceInstanceList();
        String userTypeByService = users.getUserType();
        ResultStatus rsDb = new ResultStatus();

        // PaaS-TA 서비스 형태로 제공되는 CP 포털의 사용자 등록
        // 1. 서비스 인스턴스 아이디 유효성 검사
        if(users.getServiceInstanceId().equalsIgnoreCase(NULL_REPLACE_TEXT)) {
            return resultStatusService.INVALID_SERVICE_INSTANCE_ID();
        }

        findServiceInstance = getServiceInstanceById(users.getServiceInstanceId());
        if(findServiceInstance.getItems().size() < 1) {
            return resultStatusService.INVALID_SERVICE_INSTANCE_ID();
        }

        // 2. 해당 계정이 KEYCLOAK 에 존재하는 계정인지 확인
        UsersList registerUser = checkRegisterUser(users);

        // 2-1. KEYCLOAK 미등록 사용자인 경우, 결과 메세지 리턴 처리
        if(registerUser.getResultMessage().equals(MessageConstant.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE.getMsg())) {
            return resultStatusService.USER_NOT_REGISTERED_IN_KEYCLOAK();
        }

        // 3. 아이디를 통한 사용자 상세 목록 조회
        UsersList usersListByUserId = getUsersListByUserId(users.getUserId());
        List<Users> deleteUsers = usersListByUserId.getItems().stream().filter(x-> !x.getUserAuthId().matches(users.getUserAuthId())).collect(Collectors.toList());

        // 3-1. KEYCLOAK 에서 삭제된 계정이지만, CP 에 남아있는 동일한 USER ID 의 USER 데이터, K8S SA, ROLEBINDING 삭제 진행
        for(Users du: deleteUsers) {
            usersService.deleteUsers(du);
        }

        // 4. 현재 사용자와 맵핑되어있는 네임스페이스 목록 추출
        List<Users> currentUserDetailsList = usersListByUserId.getItems().stream().filter(x-> x.getUserAuthId().matches(users.getUserAuthId())).collect(Collectors.toList());
        List<String> userNamespaceList = currentUserDetailsList.stream().map(Users::getCpNamespace).collect(Collectors.toList());

        // 4-1. temp-namespace 등록 유무 체크 후 없으면 데이터 생성
        List<Users> checkTempNamespace = currentUserDetailsList.stream().filter(x-> x.getCpNamespace().matches(propertyService.getDefaultNamespace())).collect(Collectors.toList());
        checkTempNamespace = checkTempNamespace.stream().filter(x-> x.getUserType().matches(AUTH_USER)).collect(Collectors.toList());

        if(checkTempNamespace.size() < 1) {
            users.setCpNamespace(propertyService.getDefaultNamespace());
            users.setServiceAccountName(users.getUserAuthId());
            users.setRoleSetCode(NOT_ASSIGNED_ROLE);
            users.setSaSecret(NULL_REPLACE_TEXT);
            users.setSaToken(NULL_REPLACE_TEXT);
            users.setUserType(AUTH_USER);

            LOGGER.info("CREATE USER WITH [USER] TYPE IN TEMP NAMESPACE...");
            rsDb = sendSignUpUser(users);

            if(Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
                LOGGER.info("DATABASE EXECUTE IS FAILED....TEMP NAMESPACE USER CREATE FAILED");
                return resultStatusService.CREATE_USERS_FAIL();
            }
        }

        // 5. 접속하려는 네임스페이스가 이미 사용자와 맵핑 되어있는지 체크 후 없으면 맵핑 진행
        String spaceName = "paas-" + users.getServiceInstanceId().toLowerCase() + "-caas";
        if(!userNamespaceList.contains(spaceName)){
            try {
                ServiceInstance serviceInstance = findServiceInstance.getItems().get(0);
                String addInNamespace = serviceInstance.getNamespace();
                String addSa = users.getUserAuthId();

                // 5-1. service account 생성
                resourceYamlService.createServiceAccount(addSa, addInNamespace);

                // 5-2. role binding 생성
                if(userTypeByService.equalsIgnoreCase(AUTH_NAMESPACE_ADMIN)) {
                    // 네임스페이스 관리자의 경우 ADMIN-ROLE 바인딩
                    resourceYamlService.createRoleBinding(addSa, addInNamespace, propertyService.getAdminRole());
                    users.setUserType(AUTH_NAMESPACE_ADMIN);
                    users.setRoleSetCode(propertyService.getAdminRole());
                }
                else {
                    // 일반 사용자의 경우 INIT-ROLE 바인딩
                    resourceYamlService.createRoleBinding(addSa, addInNamespace, propertyService.getInitRole());
                    users.setUserType(AUTH_USER);
                    users.setRoleSetCode(propertyService.getInitRole());
                }

                // 5-3. secret 정보 조회
                String saSecretName = restTemplateService.getSecretName(addInNamespace, addSa);

                // 5-4. user 생성
                users.setId(0);
                users.setCpNamespace(addInNamespace);
                users.setServiceAccountName(users.getUserAuthId());
                users.setIsActive(CHECK_Y);
                users.setSaSecret(saSecretName);
                users.setSaToken(accessTokenService.getSecrets(addInNamespace, saSecretName).getUserAccessToken());

                rsDb = usersService.createUsers(users);

                if(Constants.RESULT_STATUS_FAIL.equals(rsDb.getResultCode())) {
                    LOGGER.info("DATABASE EXECUTE IS FAILED....USER CREATE FAILED");
                    throw new Exception("USER CREATE FAILED");
                }
            }
            catch (Exception e) {
                // SA, RB, DB 데이터 생성 중 Exception 발생할 경우 삭제 처리
                ServiceInstance removeServiceInstance = findServiceInstance.getItems().get(0);
                String removeNamespace = removeServiceInstance.getNamespace();
                String removeSa = users.getUserAuthId();
                // SA, RB 삭제
                resourceYamlService.deleteServiceAccountAndRolebinding(removeNamespace, removeSa, users.getRoleSetCode());
                // 해당 네임스페이스 데이터 삭제
                usersService.deleteUsersByUserIdAndUserAuthIdAndNamespace(users.getUserId(), users.getUserAuthId(), removeNamespace);
                // 최초 가입의 사용자인 경우 temp-namespace 삭제
                if(!userNamespaceList.contains(propertyService.getDefaultNamespace())) {
                    usersService.deleteUsersByUserIdAndUserAuthIdAndNamespace(users.getUserId(), users.getUserAuthId(), propertyService.getDefaultNamespace());
                }

                return resultStatusService.CREATE_USERS_FAIL();
            }

        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, "/");
    }

    /**
     * Users 이름 목록 조회(Get Users names list)
     *
     * @return the Map
     */
    public Map<String, List<String>> getUsersNameList() {
        return restTemplateService.send(TARGET_COMMON_API, "/users/names", HttpMethod.GET, null, Map.class);
    }


    /**
     * 사용자 등록 여부 확인(Check for registered User)
     *
     * @param users the users
     * @return the usersList
     */
    public UsersList checkRegisterUser(Users users) {
        return restTemplateService.sendAdmin(TARGET_COMMON_API, URI_COMMON_API_CHECK_USER_REGISTER.replace("{userId:.+}", users.getUserId())
                .replace("{userAuthId:.+}", users.getUserAuthId()), HttpMethod.GET, null, UsersList.class);
    }


    /**
     * 사용자 회원가입 (Send user registration)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus sendSignUpUser(Users users) {
        return restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_SIGNUP, HttpMethod.POST, users, ResultStatus.class);
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


    /**
     * 서비스 인스턴스 정보 조회(Get serviceInstance Info)
     *
     * @param serviceInstanceId the serviceInstanceId
     * @return the serviceInstance
     */
    public ServiceInstanceList getServiceInstanceById(String serviceInstanceId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_SERVICEINSTANCE_DETAIL.replace("{serviceInstanceId:.+}", serviceInstanceId), HttpMethod.GET, null, ServiceInstanceList.class);
    }

}