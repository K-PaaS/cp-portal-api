package org.paasta.container.platform.api.users;


import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.clusters.clusters.ClustersService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.secret.Secrets;
import org.paasta.container.platform.api.users.serviceAccount.ServiceAccount;
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
    private final ClustersService clustersService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Users service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     * @param commonService       the common service
     * @param resourceYamlService the resource yaml service
     * @param accessTokenService  the access token service
     * @param clustersService     the clusters service
     */
    @Autowired
    public UsersService(RestTemplateService restTemplateService, PropertyService propertyService, CommonService commonService, ResourceYamlService resourceYamlService,
                        AccessTokenService accessTokenService, ClustersService clustersService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.resourceYamlService = resourceYamlService;
        this.accessTokenService = accessTokenService;
        this.clustersService = clustersService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Users 전체 목록 조회(Get Users list)
     *
     * @param params the params
     * @return the users list
     */
    public UsersAdminList getUsersAllByCluster(Params params) {
        String reqUrlParam = "?searchName=" + params.getSearchName().trim() ;
        if(params.getIsActive().equalsIgnoreCase(CHECK_FALSE)) {
            reqUrlParam +=  "&isActive=false";
        }

        UsersAdminList usersAdminList = restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_USERS_LIST_BY_CLUSTER.replace("{cluster:.+}", params.getCluster()) + reqUrlParam, HttpMethod.GET, null, UsersAdminList.class , params);

        // 페이징 적용
        usersAdminList = commonService.userListProcessing(usersAdminList, params, UsersAdminList.class);
        return (UsersAdminList) commonService.setResultModel(usersAdminList, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * 하나의 Cluster 내 여러 Namespaces 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)
     *
     * @param params the params
     * @return the users detail
     */
    public Object getUsersInMultiNamespace(Params params) throws Exception {
        UsersAdmin usersAdmin = new UsersAdmin();

        try {
            usersAdmin = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                    .replace("{userId:.+}", params.getUserId())
                    .replace("{userType:.+}", params.getUserType()), HttpMethod.GET, null, UsersAdmin.class, params);

            if(usersAdmin.getResultMessage().equalsIgnoreCase(MessageConstant.USER_NOT_MAPPED_TO_THE_NAMESPACE_MESSAGE.getMsg())) {
                List<UsersAdmin.UsersDetails> items = new ArrayList<>();
                usersAdmin.setItems(items);
                return commonService.setResultModel(commonService.setResultObject(usersAdmin, UsersAdmin.class), Constants.RESULT_STATUS_SUCCESS);
            }



            for (UsersAdmin.UsersDetails usersDetails : usersAdmin.getItems()) {

                //serviceAccount 조회
                Object sa_obj = restTemplateService.send(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersGetUrl()
                        .replace("{namespace}", usersDetails.getCpNamespace())
                        .replace("{name}", usersAdmin.getServiceAccountName()), HttpMethod.GET, null, Map.class, params);


                if (!(sa_obj instanceof ResultStatus)) {
                    // k8s에서 serviceAccount 정보 조회(Get SA from k8s)
                    ServiceAccount serviceAccount = commonService.setResultObject(sa_obj, ServiceAccount.class);
                    usersDetails.setServiceAccountUid(serviceAccount.getMetadata().getUid());
                }

                //secret 조회
                Object obj = restTemplateService.send(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListSecretsGetUrl()
                        .replace("{namespace}", usersDetails.getCpNamespace())
                        .replace("{name}", usersDetails.getSaSecret()), HttpMethod.GET, null, Map.class, params);

                if (!(obj instanceof ResultStatus)) {
                    // k8s에서 secret 정보 조회(Get secret from k8s)
                    Secrets secrets = (Secrets) commonService.setResultModel(commonService.setResultObject(obj, Secrets.class), Constants.RESULT_STATUS_SUCCESS);
                    usersDetails.setSecrets(UsersAdmin.Secrets.builder()
                            .saSecret(secrets.getMetadata().getName())
                            .secretLabels(secrets.getMetadata().getLabels())
                            .secretType(secrets.getType()).build()); }
            }

        } catch (Exception e) {
            return resultStatusService.NOT_FOUND_RESULT_STATUS();
        }

        usersAdmin = commonService.userListProcessing(usersAdmin, params, UsersAdmin.class);
        return commonService.setResultModel(usersAdmin, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Users 수정(Update Users)
     *
     * @param params the params
     * @param users   the users
     * @return the resultStatus
     * @throws Exception
     */
    public ResultStatus modifyUsersAdmin(Params params, Users users) throws Exception {
        ResultStatus rsDb = new ResultStatus();


        UsersList currentUserNsMappingList = restTemplateService.send(Constants.TARGET_COMMON_API, URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_NAME_USER_ID
                .replace("{cluster:.+}", params.getCluster())
                .replace("{userId:.+}", params.getUserId()), HttpMethod.GET, null, UsersList.class, params);

        List<Users.NamespaceRole> selectValues = users.getSelectValues();

        // 기존 사용자 namespace list(Existed namespace list)
        List<String> defaultNsList = currentUserNsMappingList.getItems().stream().map(Users::getCpNamespace).collect(Collectors.toList());

        // 넘어온 새로운 select value 중 namespace list(namespace list for new select value)
        List<String> newNsList = selectValues.stream().map(Users.NamespaceRole::getNamespace).collect(Collectors.toList());

        ArrayList<String> asIs = commonService.equalArrayList(defaultNsList, newNsList);
        ArrayList<String> toBeDelete = commonService.compareArrayList(defaultNsList, newNsList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(newNsList, defaultNsList);

        List<Users.NamespaceRole> asIsNamespaces = new ArrayList<>();
        List<Users.NamespaceRole> toBeAddNamespace = new ArrayList<>();


        for (Users.NamespaceRole namespaceRole : selectValues) {
            Users.NamespaceRole namespaceRole2 = new Users.NamespaceRole();
            for (String name : asIs) {
                if (namespaceRole.getNamespace().equals(name)) {
                    namespaceRole2.setNamespace(namespaceRole.getNamespace());
                    namespaceRole2.setRole(namespaceRole.getRole());

                    asIsNamespaces.add(namespaceRole2);
                }
            }

            for (String name : toBeAdd) {
                if (namespaceRole.getNamespace().equals(name)) {
                    namespaceRole2.setNamespace(namespaceRole.getNamespace());
                    namespaceRole2.setRole(namespaceRole.getRole());

                    toBeAddNamespace.add(namespaceRole2);
                }
            }
        }

        for (Users.NamespaceRole nr : asIsNamespaces) {
            Users updateUser = getUsers(params.getCluster(), nr.getNamespace(), users.getUserId());
            String namespace = nr.getNamespace();
            String newRole = nr.getRole();
            String defaultRole = updateUser.getRoleSetCode();

            if (!updateUser.getRoleSetCode().equals(nr.getRole())) {
                LOGGER.info("Same Namespace >> {}, Default Role >> {}, New Role >> {}", CommonUtils.loggerReplace(namespace), CommonUtils.loggerReplace(defaultRole), CommonUtils.loggerReplace(newRole));
                restTemplateService.send(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListRoleBindingsDeleteUrl().
                                replace("{namespace}", namespace).replace("{name}", users.getServiceAccountName() + Constants.NULL_REPLACE_TEXT + defaultRole + "-binding"),
                        HttpMethod.DELETE, null, Object.class, params);

                params.setNamespace(namespace);
                params.setRs_sa(users.getServiceAccountName());
                params.setRs_role(newRole);
                resourceYamlService.createRoleBinding(params);


                updateUser.setRoleSetCode(newRole);
                updateUser.setSaToken(accessTokenService.getSecrets(namespace, updateUser.getSaSecret()).getUserAccessToken());
            }

            updateUser.setUserId(users.getUserId());
            rsDb = createUsers(updateUser);
        }


        for (String deleteSa : toBeDelete) {
            LOGGER.info("Default Namespace's service account delete >> " + CommonUtils.loggerReplace(deleteSa));
            Users deleteUser = getUsers(params.getCluster(), deleteSa, users.getUserId());
            deleteUsers(deleteUser);
        }


        for (Users.NamespaceRole nr : toBeAddNamespace) {
            String addInNamespace = nr.getNamespace();
            String addRole = nr.getRole();
            String sa = users.getServiceAccountName();

            LOGGER.info("New Namespace create >> {}, New Role >> {}", CommonUtils.loggerReplace(addInNamespace), CommonUtils.loggerReplace(addRole));


            params.setNamespace(addInNamespace);
            params.setRs_sa(sa);
            params.setRs_role(addRole);

            resourceYamlService.createServiceAccount(params);
            resourceYamlService.createRoleBinding(params);

            String saSecretName = restTemplateService.getSecretName(addInNamespace, sa);

            Users newUser = users;
            newUser.setId(0);
            newUser.setCpNamespace(addInNamespace);
            newUser.setRoleSetCode(addRole);
            newUser.setIsActive(CHECK_Y);
            newUser.setSaSecret(saSecretName);
            newUser.setSaToken(accessTokenService.getSecrets(addInNamespace, saSecretName).getUserAccessToken());
            newUser.setUserType(AUTH_USER);

            rsDb = createUsers(newUser);
        }

        ResultStatus finalRs = (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rsDb, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, Constants.URI_USERS_DETAIL.replace("{userId:.+}", users.getUserId()));
        if(Constants.RESULT_STATUS_SUCCESS.equals(finalRs.getResultCode())) {
            finalRs.setResultMessage(CommonStatusCode.OK.getMsg());
            finalRs.setHttpStatusCode(CommonStatusCode.OK.getCode());
            finalRs.setDetailMessage(CommonStatusCode.OK.getMsg());
        }

        return finalRs;
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
     * 클러스터 관리자 조회(Get cluster admin)
     *
     * @return the usersList
     */
    public UsersListAdmin getClusterAdminAllByCluster(Params params) {
        UsersListAdmin usersListAdmin = restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_CLUSTER_ADMIN_INFO.replace("{searchName:.+}", params.getSearchName().trim()), HttpMethod.GET, null, UsersListAdmin.class, params);
        usersListAdmin = commonService.userListProcessing(usersListAdmin, params, UsersListAdmin.class);
        return (UsersListAdmin) commonService.setResultModel(usersListAdmin, Constants.RESULT_STATUS_SUCCESS);
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


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//사용자 포탈 기능, 운영자 포탈에 적용될 수 있으므로 보류



    /**
     * Users 전체 목록 조회(Get Users list)
     *
     * @param cluster the cluster
     * @param namespace the namespace
     * @param userId the userId
     * @return the users list
     */
    public UsersListAdmin getUsersAll(String cluster,String namespace, String userId) {

        Users users = getUsers(cluster, namespace, userId);

        if(users == null || !users.getUserType().equals(AUTH_NAMESPACE_ADMIN)) {
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
        UsersList usersList =  restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
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
     * Users 로그인을 위한 상세 조회(Get Users for login)
     *
     * @param userId  the userId
     * @param isAdmin the isAdmin
     * @return the users detail
     */
    public Users getUsersDetailsForLogin(String userId, String isAdmin) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DETAIL_LOGIN.replace("{userId:.+}", userId)
                        + "?isAdmin=" + isAdmin
                , HttpMethod.GET, null, Users.class);
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
     * Users 상세 조회(Get Users detail)
     *
     * @param userId the userId
     * @return the users detail
     */
    public UsersList getUsersDetails(String userId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_DETAIL.replace("{userId:.+}", userId), HttpMethod.GET, null, UsersList.class);
    }


    /**
     * Namespace 와 userId로 사용자 단 건 상세 조회(Get Users userId namespace)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the userId
     * @return the users detail
     */
    public Users getUsers(String cluster, String namespace, String userId) {
        Users users = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace)
                .replace("{userId:.+}", userId), HttpMethod.GET, null, Users.class);
        return (Users) commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 DB 저장(Save Users DB)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus createUsers(Users users) {
        return restTemplateService.sendAdmin(TARGET_COMMON_API, "/users", HttpMethod.POST, users, ResultStatus.class);
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
     * Users 삭제(Delete Users)
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus deleteUsers(Users users) {
        String namespace = users.getCpNamespace();
        String saName = users.getServiceAccountName();
        String role = users.getRoleSetCode();

        // 기존 service account 삭제(Delete Exited service account)
        restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", namespace).replace("{name}", saName), HttpMethod.DELETE, null, Object.class, true);

        // role binding 삭제(Delete role binding)
        restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListRoleBindingsDeleteUrl().replace("{namespace}", namespace).replace("{name}", saName + Constants.NULL_REPLACE_TEXT + role + "-binding"), HttpMethod.DELETE, null, Object.class, true);

        // DB delete
        ResultStatus rsDb = (ResultStatus) restTemplateService.sendAdmin(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DELETE + users.getId(), HttpMethod.DELETE, null, Object.class);

        return rsDb;
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


    /**
     * Users 삭제(Delete Users)
     * (All Namespaces)
     *
     * @param userId the user id
     * @return return is succeeded
     */
    public ResultStatus deleteUsersByAllNamespaces(String userId) {
        UsersList users = getUsersDetails(userId);

        for(Users user : users.getItems()) {
            if (user.getUserType().equals(AUTH_CLUSTER_ADMIN)) {
                return resultStatusService.DO_NOT_DELETE_DEFAULT_RESOURCES();
            }
        }

        ResultStatus rs = new ResultStatus();
        for (Users user : users.getItems()) {
            rs = deleteUsers(user);
        }
        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rs, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_USERS);
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
     * 해당 클러스터 정보의 값을 사용자에 저장
     *
     * @param clusterName the cluster name
     * @param users       the users
     * @return the users
     */
    public Users commonSaveClusterInfo(String clusterName, Users users) {
        Clusters clusters = clustersService.getClusters(clusterName);

        users.setClusterApiUrl(clusters.getClusterApiUrl());
        users.setClusterName(clusters.getClusterName());
        users.setClusterToken(clusters.getClusterToken());

        return users;
    }



    /**
     * CLUSTER_ADMIN 권한을 가진 운영자 상세 조회(Get Cluster Admin's info)
     *
     * @param cluster   the cluster
     * @param userId    the userId
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
     * 사용자 DB 저장(Save Users DB) Encode 용
     *
     * @param users the users
     * @return return is succeeded
     */
    public ResultStatus createUsersForEncode(Users users) {
        String param = "?encode=" + CHECK_Y;
        return restTemplateService.sendAdmin(TARGET_COMMON_API, "/users" + param, HttpMethod.POST, users, ResultStatus.class);
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
     * User ID 중복 체크(Duplication check User ID)
     *
     * @param users the users
     * @return the boolean
     */
    public Boolean duplicatedUserIdCheck(Users users) {
        Boolean isDuplicated = false;
        List<String> list = getUsersNameListByDuplicated().get(Constants.USERS);
        for (String name:list) {
            if (name.equals(users.getUserId())) {
                isDuplicated = true;
            }
        }
        return isDuplicated;
    }






    /**
     * 사용자 아이디, 사용자 인증 아이디, 네임스페이스를 통한 Users 삭제 (Delete Users by userId, userAuthId and namespace)
     *
     * @param userId the userId
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


}