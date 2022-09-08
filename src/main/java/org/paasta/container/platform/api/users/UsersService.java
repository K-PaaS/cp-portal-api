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

        try{
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
        }
        catch(Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("params.getCluster(): " + params.getCluster());
        System.out.println("params.getNamespace() :" + params.getNamespace());
        System.out.println("params.getId(): " + params.getId());
        System.out.println("params.getUserAuthId():"+ params.getUserAuthId());
        System.out.println("params.getUserType(): "+ params.getUserType());
        System.out.println("params.getRs_sa():" + params.getRs_sa());
        System.out.println("params.getRs_role():" + params.getRs_role());
        System.out.println("-----------------------------------------------------------------------------------------------------");

        return (ResultStatus) commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS);
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
            // 클러스터 내 사용자가 맵핑되어있는 SA, ROLE-BINDING, DB 데이터 삭제 진행
            for (Users u : usersDetails.getItems()) {
                resourceYamlService.deleteUserResource(u);
            }

            // 클러스터 관리자 관련 resource 생성
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



    public ResultStatus deleteNamespaceAllUsers(Params params){
        // 클러스터 내 특정 네임스페이스 사용자 db 데이터 조회
        UsersList usersList = getAllUsersListByClusterAndNamespaces(params);

        // vault user token 삭제
        for(Users users : usersList.getItems()) {
            Params deleteUser = new Params(users.getClusterId(), users.getUserAuthId(), users.getUserType(), users.getCpNamespace());
            vaultService.deleteUserAccessToken(deleteUser);
        }

        // db 데이터 삭제
        ResultStatus resultStatus = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace()), HttpMethod.DELETE, null, ResultStatus.class, params);

        return resultStatus;
    }



    public UsersList getAllUsersListByClusterAndNamespaces(Params params) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace()), HttpMethod.GET, null, UsersList.class, params);
    }

}