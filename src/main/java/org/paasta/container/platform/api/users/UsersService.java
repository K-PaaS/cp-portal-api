package org.paasta.container.platform.api.users;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.ResultStatusException;
import org.paasta.container.platform.api.secret.Secrets;
import org.paasta.container.platform.api.users.support.NamespaceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * User Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 **/
@Service
public class UsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private final ResourceYamlService resourceYamlService;
    private final AccessTokenService accessTokenService;
    private final ResultStatusService resultStatusService;
    private final VaultService vaultService;

    /**
     * Instantiates a new Users service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     * @param commonService       the common service
     * @param resourceYamlService the resource yaml service
     * @param accessTokenService  the access token service
     */
    @Autowired
    public UsersService(RestTemplateService restTemplateService, PropertyService propertyService, CommonService commonService, ResourceYamlService resourceYamlService,
                        AccessTokenService accessTokenService, ResultStatusService resultStatusService, VaultService vaultService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.resourceYamlService = resourceYamlService;
        this.accessTokenService = accessTokenService;
        this.resultStatusService = resultStatusService;
        this.vaultService = vaultService;
    }


    /**
     * Users 전체 목록 조회(Get Users list)
     * 개발 0809 사용자 목록 조회 - active
     *
     * @param params the params
     * @return the users list
     */
    public UsersDetailsList getUsersAllByCluster(Params params) {
        String reqUrlParam = "?searchName=" + params.getSearchName().trim();
        if (params.getIsActive().equalsIgnoreCase(CHECK_FALSE)) {
            reqUrlParam += "&isActive=false";
        }

        UsersDetailsList usersDetailsList = restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_USERS_LIST_BY_CLUSTER
                        .replace("{cluster:.+}", params.getCluster())
                        .replace("{namespace:.+}", params.getNamespace()) + reqUrlParam, HttpMethod.GET,
                null, UsersDetailsList.class, params);

        // 페이징 적용
        usersDetailsList = commonService.userListProcessing(usersDetailsList, params, UsersDetailsList.class);
        return (UsersDetailsList) commonService.setResultModel(usersDetailsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 하나의 Cluster 내 여러 Namespaces 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)
     * 개발 0811 사용자 상세 조회
     *
     * @param params the params
     * @return the users detail
     */
    public UsersDetails getUsersDetailsByCluster(Params params) {
        UsersDetails usersDetails = new UsersDetails();
        try {
            usersDetails = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                    .replace("{cluster:.+}", params.getCluster())
                    .replace("{userAuthId:.+}", params.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, params);

            System.out.println(usersDetails);

            for (Users user : usersDetails.getItems()) {
                if (user.getUserType().equalsIgnoreCase(AUTH_CLUSTER_ADMIN)) {
                    user.setCpNamespace(propertyService.getClusterAdminNamespace());
                }
                Params p = new Params(user.getClusterId(), user.getCpNamespace(), user.getSaSecret(), true);
                Secrets secrets = resourceYamlService.getSecret(p);
                user.setSecretName(secrets.getMetadata().getName());
                user.setSecretUid(secrets.getMetadata().getUid());
                user.setSecretCreationTimestamp(secrets.getMetadata().getCreationTimestamp());
            }

        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.NOT_FOUND.getMsg());
        }

        return (UsersDetails) commonService.setResultModel(usersDetails, Constants.RESULT_STATUS_SUCCESS);
    }


    public Object getUsersAccessInfo(Params params) throws Exception {
        UsersAdmin usersAdmin = new UsersAdmin();

        usersAdmin = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_INFO_USER_DETAILS
                .replace("{userAuthId:.+}", params.getUserAuthId())
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace()), HttpMethod.GET, null, UsersAdmin.class, params);

        params.setUserType(usersAdmin.getItems().get(0).getUserType());

        accessTokenService.getVaultSecrets(params);

        usersAdmin.setClusterApiUrl(params.getClusterApiUrl());
        usersAdmin.setClusterName(params.getCluster());
        usersAdmin.setClusterToken(params.getClusterToken());

        usersAdmin = commonService.userListProcessing(usersAdmin, params, UsersAdmin.class);
        return commonService.setResultModel(usersAdmin, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Users 수정(Update Users)
     *
     * @param params the params
     * @param users  the users
     * @return the resultStatus
     * @throws Exception
     */
    public ResultStatus modifyToUser(Params params, Users users) throws Exception {
        ResultStatus rsDb = new ResultStatus();

        params.setUserAuthId(users.getUserAuthId());
        UsersDetails usersDetails = getUsersDetailByCluster(params);
        List<Users> currentUserNsMappingList = usersDetails.getItems();

        // CLUSTER ADMIN -> USER 권한 변경의 경우 CLUSTER ADMIN 권한 제거
        if (usersDetails.getUserType().equalsIgnoreCase(AUTH_CLUSTER_ADMIN)) {
            Users clusterAdminInfo = currentUserNsMappingList.get(0);
            resourceYamlService.deleteClusterAdminResource(clusterAdminInfo);
            currentUserNsMappingList = new ArrayList<>();
        }

        List<NamespaceRole> newNamespaceRoleList = users.getSelectValues();
        List<NamespaceRole> currentNamespaceRoleList = currentUserNsMappingList.stream().map(x -> new NamespaceRole(x.getCpNamespace(), x.getRoleSetCode())).collect(Collectors.toList());

        List<String> newNamespacesList = newNamespaceRoleList.stream().map(NamespaceRole::getNamespace).collect(Collectors.toList());
        List<String> currentNamespacesList = currentUserNsMappingList.stream().map(Users::getCpNamespace).collect(Collectors.toList());


        List<NamespaceRole> asis = newNamespaceRoleList.stream().filter(x -> currentNamespacesList.contains(x.getNamespace())).collect(Collectors.toList());
        List<NamespaceRole> toBeDelete = currentNamespaceRoleList.stream().filter(x -> !newNamespacesList.contains(x.getNamespace())).collect(Collectors.toList());
        List<NamespaceRole> toBeAdd = newNamespaceRoleList.stream().filter(x -> !currentNamespacesList.contains(x.getNamespace())).collect(Collectors.toList());


        System.out.println("asis:" + asis.toString());
        System.out.println("toBeDelete:" + toBeDelete.toString());
        System.out.println("toBeAdd:" + toBeAdd.toString());


        for (NamespaceRole newNr : asis) {
            NamespaceRole currentNr = currentNamespaceRoleList.stream().filter(x -> x.getNamespace().equals(newNr.getNamespace())).collect(Collectors.toList()).get(0);
            if (!currentNr.getRole().equals(newNr.getRole())) {
                toBeDelete.add(currentNr);
                toBeAdd.add(newNr);
            }
        }

        System.out.println("---------------------------------");
        System.out.println("asis:" + asis.toString());
        System.out.println("toBeDelete:" + toBeDelete.toString());
        System.out.println("toBeAdd:" + toBeAdd.toString());


        if (usersDetails.getUserType().equalsIgnoreCase(AUTH_USER) && toBeDelete.size() < 1 && toBeAdd.size() < 1) {
            throw new ResultStatusException(MessageConstant.NO_CHANGED.getMsg());
        }


        // to be delete
        for (NamespaceRole nr : toBeDelete) {
            Users user = findUsers(currentUserNsMappingList, nr.getNamespace());
            resourceYamlService.deleteUserResource(user);
        }


        // to be add
        for (NamespaceRole nr : toBeAdd) {
            params.setNamespace(nr.getNamespace());
            params.setRs_role(nr.getRole());
            resourceYamlService.createUserResource(params, users);
        }

        return (ResultStatus) commonService.setResultModel(rsDb, Constants.RESULT_STATUS_SUCCESS);
    }


    public Users findUsers(List<Users> userList, String namespace) {
        for (Users users : userList) {
            if (users.getCpNamespace().equals(namespace)) {
                return users;
            }
        }
        return null;
    }


    /**
     * Namespace Users 목록 조회(Get Users List By Namespaces)
     *
     * @param params the params
     * @return the users list
     */
    public Object getUsersListInNamespaceAdmin(Params params) {
        String param = "?orderBy=" + params.getOrderBy() + "&order=" + params.getOrder() + "&searchName=" + params.getSearchName();
        UsersListInNamespaceAdmin usersListInNamespaceAdmin = restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace()) + param, HttpMethod.GET, null, UsersListInNamespaceAdmin.class, params);

        //users list paging
        usersListInNamespaceAdmin = commonService.userListProcessing(usersListInNamespaceAdmin, params, UsersListInNamespaceAdmin.class);
        return (UsersListInNamespaceAdmin) commonService.setResultModel(usersListInNamespaceAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 특정 Namespace 관리자 판별이 포함된 Users Name 목록 조회
     *
     * @param params the params
     * @return the UsersInNamespace
     */
    public UsersInNamespace getUsersNameListByNamespaceAdmin(Params params) {
        UsersInNamespace usersInNamespace = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_NAMESPACE_OR_NOT_CHECK
                .replace("{cluster:.+}", params.getCluster()).replace("{namespace:.+}", params.getNamespace()), HttpMethod.GET, null, UsersInNamespace.class, params);
        usersInNamespace.setNamespace(params.getNamespace());
        return (UsersInNamespace) commonService.setResultModel(usersInNamespace, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * User 과 맵핑된 클러스터 목록 조회
     *
     * @param params the params
     * @return the UsersList
     */
    public UsersList getMappingClustersListByUser(Params params) {
        UsersList usersList = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_LIST_BY_USER
                        .replace("{userAuthId:.+}", params.getUserAuthId()).replace("{userType:.+}", params.getUserType()),
                HttpMethod.GET, null, UsersList.class, params);

        if (params.getIsGlobal()) {
            // global 화면의 경우 SUPER-ADMIN, CLUSTER-ADMIN 권한과 맵핑된 클러스터 목록 반환
            List<Users> items = usersList.getItems().stream().filter(x -> !x.getUserType().equals(AUTH_USER)).collect(Collectors.toList());
            usersList.setItems(items);
        }
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * User 과 맵핑된 클러스터 & 네임스페이스 목록 조회
     *
     * @param params the params
     * @return the UsersList
     */
    public UsersList getMappingClustersAndNamespacesListByUser(Params params) {
        UsersList usersList = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_AND_NAMESPACE_LIST_BY_USER
                        .replace("{userAuthId:.+}", params.getUserAuthId()).replace("{userType:.+}", params.getUserType()),
                HttpMethod.GET, null, UsersList.class, params);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * User 과 맵핑된 네임스페이스 목록 조회
     *
     * @param params the params
     * @return the UsersList
     */
    public UsersList getMappingNamespacesListByUser(Params params) {
        UsersList usersList = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_USER_AUTH_ID
                        .replace("{cluster:.+}", params.getCluster())
                        .replace("{userAuthId:.+}", params.getUserAuthId()),
                HttpMethod.GET, null, UsersList.class, params);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//사용자 포탈 기능, 운영자 포탈에 적용될 수 있으므로 보류


    /**
     * Users 전체 목록 조회(Get Users list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the userId
     * @return the users list
     */
    public UsersListAdmin getUsersAll(String cluster, String namespace, String userId) {

        Users users = getUsers(cluster, namespace, userId);

        if (users == null || !users.getUserType().equals(AUTH_NAMESPACE_ADMIN)) {
            UsersListAdmin usersListAdmin = new UsersListAdmin();
            usersListAdmin.setResultCode(RESULT_STATUS_FAIL);
            usersListAdmin.setResultMessage(CommonStatusCode.FORBIDDEN.getMsg());
            usersListAdmin.setDetailMessage(CommonStatusCode.FORBIDDEN.getMsg());
            return usersListAdmin;
        }

        UsersListAdmin rsDb = restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST + "?namespace=" + namespace, HttpMethod.GET, null, UsersListAdmin.class);

        return (UsersListAdmin) commonService.setResultModel(commonService.setResultObject(rsDb, UsersListAdmin.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users list
     */
    public UsersListAdmin getUsersListByNamespaceAdmin(String cluster, String namespace) {
        return restTemplateService.sendAdmin(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace), HttpMethod.GET, null, UsersListAdmin.class);
    }


    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users list
     */
    public UsersList getUsersListByNamespace(String cluster, String namespace) {
        UsersList usersList = restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace), HttpMethod.GET, null, UsersList.class);

        for (Users users : usersList.getItems()) {
            users.setClusterApiUrl(Constants.NULL_REPLACE_TEXT);
            users.setClusterToken(Constants.NULL_REPLACE_TEXT);
            users.setPassword(Constants.NULL_REPLACE_TEXT);
            users.setSaSecret(Constants.NULL_REPLACE_TEXT);
            users.setSaToken(Constants.NULL_REPLACE_TEXT);
        }

        return usersList;
    }


    /**
     * 각 Namespace 별 등록 되어 있는 사용자들의 이름 목록 조회(Get Users registered list namespace)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users list
     */
    public Map<String, List> getUsersNameListByNamespace(String cluster, String namespace) {
        return restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_NAMES_LIST
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace), HttpMethod.GET, null, Map.class);
    }


    /**
     * Namespace 관리자 상세 조회(Get Namespace Admin Users detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users detail
     */
    public Users getUsersByNamespaceAndNsAdmin(String cluster, String namespace) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_BY_NAMESPACE_NS_ADMIN.replace("{cluster:.+}", cluster).replace("{namespace:.+}", namespace)
                , HttpMethod.GET, null, Users.class);
    }


    /**
     * Namespace 와 userId로 사용자 단 건 상세 조회(Get Users userId namespace)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param userAuthId the userAuthId
     * @return the users detail
     */
    public Users getUsers(String cluster, String namespace, String userAuthId) {
        Users users = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace)
                .replace("{userAuthId:.+}", userAuthId), HttpMethod.GET, null, Users.class);
        return (Users) commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 DB 저장(Save Users DB)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus createUsers(Users users) {
        return restTemplateService.send(TARGET_COMMON_API, "/users", HttpMethod.POST, users, ResultStatus.class, new Params());
    }


    /**
     * 사용자 권한 변경 DB 저장(Save Users DB)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus updateUsers(Users users) {
        return restTemplateService.sendAdmin(TARGET_COMMON_API, "/users", HttpMethod.PUT, users, ResultStatus.class);
    }


    /**
     * Users 수정(Update Users)
     *
     * @param userId the user id
     * @param user   the users
     * @return return is succeeded
     */
    public ResultStatus modifyUsers(String userId, Users user) {
        return restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_DETAIL.replace("{userId:.+}", userId), HttpMethod.PUT, user, ResultStatus.class);
    }


//    /**
//     * Users 권한 설정(Set Users authority)
//     *
//     * @param cluster   the cluster
//     * @param namespace the namespace
//     * @param users     the users
//     * @return return is succeeded
//     */
//    public ResultStatus modifyUsersConfig(String cluster, String namespace, List<Users> users) {
//        ResultStatus rsDb = new ResultStatus();
//
//        List<Users> defaultUserList = getUsersListByNamespace(cluster, namespace).getItems();
//
//        List<String> defaultUserNameList = defaultUserList.stream().map(Users::getServiceAccountName).collect(Collectors.toList());
//        List<String> newUserNameList = users.stream().map(Users::getServiceAccountName).collect(Collectors.toList());
//
//        ArrayList<String> toBeDelete = (ArrayList<String>) defaultUserNameList.stream().filter(x -> !newUserNameList.contains(x)).collect(Collectors.toList());
//        ArrayList<String> toBeAdd = (ArrayList<String>) newUserNameList.stream().filter(x -> !defaultUserNameList.contains(x)).collect(Collectors.toList());
//
//        for (Users value : defaultUserList) {
//            for (Users u : users) {
//                String sa = u.getServiceAccountName();
//                String role = u.getRoleSetCode();
//                String userId = u.getUserId();
//
//                if (value.getServiceAccountName().equals(sa)) {
//                    if (!value.getRoleSetCode().equals(role)) {
//                        LOGGER.info("Update >>> sa :: {}, role :: {}", CommonUtils.loggerReplace(sa), CommonUtils.loggerReplace(role));
//
//                        Users updatedUser = getUsers(cluster, namespace, userId);
//
//                        // remove default roleBinding, add new roleBinding
//                        restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListRoleBindingsDeleteUrl().replace("{namespace}", namespace).replace("{name}", sa + Constants.NULL_REPLACE_TEXT + value.getRoleSetCode() + "-binding"), HttpMethod.DELETE, null, Object.class, true);
//
//                        updateSetRoleUser(namespace, sa, role, updatedUser);
//                        updatedUser.setRoleSetCode(role);
//                        rsDb = updateUsers(updatedUser);
//                    }
//                }
//            }
//
//            for (String s : toBeDelete) {
//                if (s.equals(value.getServiceAccountName())) {
//                    String saName = value.getServiceAccountName();
//                    String roleName = value.getRoleSetCode();
//                    String userId = value.getUserId();
//
//                    LOGGER.info("Delete >>> sa :: {}, role :: {}", CommonUtils.loggerReplace(saName), CommonUtils.loggerReplace(roleName));
//
//                    restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", namespace).replace("{name}", saName), HttpMethod.DELETE, null, Object.class, true);
//                    restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListRoleBindingsDeleteUrl().replace("{namespace}", namespace).replace("{name}", saName + Constants.NULL_REPLACE_TEXT + roleName + "-binding"), HttpMethod.DELETE, null, Object.class, true);
//
//                    rsDb = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS.replace("{cluster:.+}", cluster).replace("{namespace:.+}", namespace).replace("{userId:.+}", userId), HttpMethod.DELETE, null, ResultStatus.class);
//                }
//            }
//        }
//
//        for (Users user : users) {
//            for (String s : toBeAdd) {
//                if (s.equals(user.getServiceAccountName())) {
//                    String saName = user.getServiceAccountName();
//                    String roleName = user.getRoleSetCode();
//                    String userId = user.getUserId();
//
//                    LOGGER.info("Add >>> sa :: {}, role :: {}", CommonUtils.loggerReplace(saName), CommonUtils.loggerReplace(roleName));
//
//                    UsersList usersList = getUsersDetails(userId);
//                    Users newUser = usersList.getItems().get(0);
//
//                    resourceYamlService.createServiceAccount(saName, namespace);
//
//                    updateSetRoleUser(namespace, saName, roleName, newUser);
//                    newUser.setId(0);
//                    newUser.setCpNamespace(namespace);
//                    newUser.setRoleSetCode(roleName);
//                    newUser.setIsActive(CHECK_Y);
//                    newUser.setUserType(AUTH_USER);
//
//                    rsDb = updateUsers(newUser);
//                }
//            }
//        }
//
//
//        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class),
//                Constants.RESULT_STATUS_SUCCESS, Constants.URI_USERS_CONFIG);
//    }


//    /**
//     * Role 에 따른 사용자 권한 설정(Setting Role to User)
//     *
//     * @param namespace the namespace
//     * @param saName    the service account name
//     * @param roleName  the role name
//     * @param newUser   the new User object
//     */
//    private void updateSetRoleUser(String namespace, String saName, String roleName, Users newUser) {
//        if (!Constants.NOT_ASSIGNED_ROLE.equals(roleName)) {
//            resourceYamlService.createRoleBinding(saName, namespace, roleName);
//            String saSecretName = restTemplateService.getSecretName(namespace, saName);
//            newUser.setSaSecret(saSecretName);
//            newUser.setSaToken(accessTokenService.getSecrets(namespace, saSecretName).getUserAccessToken());
//        } else {
//            newUser.setSaSecret(Constants.NOT_ASSIGNED_ROLE);
//            newUser.setSaToken(Constants.NOT_ASSIGNED_ROLE);
//        }
//
//    }


    /**
     * CLUSTER_ADMIN 권한을 가진 운영자 상세 조회(Get Cluster Admin's info)
     *
     * @param cluster the cluster
     * @param userId  the userId
     * @return the users detail
     */
    public Users getClusterAdminUsers(String cluster, String userId) {
        Users users = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_ADMIN_ROLE_BY_CLUSTER_NAME_USER_ID
                .replace("{cluster:.+}", cluster)
                .replace("{userId:.+}", userId), HttpMethod.GET, null, Users.class);


        Users tempUser = getUsers(cluster, propertyService.getDefaultNamespace(), userId);
        users.setEmail(tempUser.getEmail());

        return (Users) commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Users 이름 목록 조회(Get Users names list)
     *
     * @return the Map
     */
    public Map<String, List<String>> getUsersNameListByDuplicated() {
        return restTemplateService.send(TARGET_COMMON_API, "/users/names", HttpMethod.GET, null, Map.class);
    }


    /**
     * 사용자 아이디, 사용자 인증 아이디, 네임스페이스를 통한 Users 삭제 (Delete Users by userId, userAuthId and namespace)
     *
     * @param userId     the userId
     * @param userAuthId the userAuthId
     * @return the resultStatus
     */
    public ResultStatus deleteUsersByUserIdAndUserAuthIdAndNamespace(String userId, String userAuthId, String namespace) {
        // DB delete
        ResultStatus rsDb = (ResultStatus) restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_DELETE_USER_BY_ID_AND_AUTHID
                .replace("{userId:.+}", userId)
                .replace("{userAuthId:.+}", userAuthId)
                .replace("{namespace:.+}", namespace), HttpMethod.DELETE, null, Object.class);

        return rsDb;
    }


    /**
     * 클러스터 관리자로 권한 변경
     *
     * @param params the params
     * @param users  the users
     * @return the resultStatus
     */
    public ResultStatus modifyToClusterAdmin(Params params, Users users) {
        params.setUserAuthId(users.getUserAuthId());
        UsersDetails usersDetails = getUsersDetailByCluster(params);


        // 클러스터 관리자 -> 클러스터 관리자의 경우 변경 사항 없음 메세지 반환
        if (usersDetails.getUserType().equalsIgnoreCase(AUTH_CLUSTER_ADMIN)) {
            throw new ResultStatusException(MessageConstant.NO_CHANGED.getMsg());
        }

        try {
            // 해당 클러스터 내 사용자가 맵핑되어있는 K8S SA, ROLEBINDING, DB 데이터 삭제 진행
            for (Users u : usersDetails.getItems()) {
                resourceYamlService.deleteUserResource(u);
            }
        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }


        try {
            // 해당 클러스터 내 클러스터 관리자 관련 resource 생성
            resourceYamlService.createClusterAdminResource(params, users);

        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED WHILE MODIFY TO CLUSTER ADMIN ...");
            users.setClusterId(params.getCluster());
            resourceYamlService.deleteClusterAdminResource(users);
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        return (ResultStatus) commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 클러스터 관리자 조회(Get cluster admin)
     *
     * @return the usersList
     */
    public UsersListAdmin getClusterAdminListByCluster(Params params) {
        UsersListAdmin usersListAdmin = restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_CLUSTER_ADMIN_LIST
                        .replace("{cluster:.+}", params.getCluster())
                        .replace("{searchName:.+}", params.getSearchName().trim()), HttpMethod.GET, null, UsersListAdmin.class, params);
        usersListAdmin = commonService.userListProcessing(usersListAdmin, params, UsersListAdmin.class);
        return (UsersListAdmin) commonService.setResultModel(usersListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Users 삭제(Delete Users)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deleteUsers(Params params) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_DELETE_USER
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace())
                .replace("{userAuthId:.+}", params.getUserAuthId())
                .replace("{userType:.+}", params.getUserType()), HttpMethod.DELETE, null, ResultStatus.class, params);
    }

    public ResultStatus deleteUsers(List<Long> ids) {
        String sendParams = StringUtils.join(ids, ",");
        System.out.println("sendParams:" + sendParams);
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_DELETE_USER_BY_IDS + sendParams,
                HttpMethod.DELETE, null, ResultStatus.class, new Params());

    }

    public UsersDetails getUsersDetailByCluster(Params params) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                .replace("{cluster:.+}", params.getCluster())
                .replace("{userAuthId:.+}", params.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, params);
    }


}