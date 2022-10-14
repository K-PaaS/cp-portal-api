package org.paasta.container.platform.api.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.paasta.container.platform.api.accessInfo.AccessToken;
import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.clusters.clusters.ClustersService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.secret.Secrets;
import org.paasta.container.platform.api.users.support.NamespaceRole;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.paasta.container.platform.api.common.Constants.*;

/**
 *  Users Service Test 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.11.13
 **/
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class UsersServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String CLUSTER_API_URL = "111.111.111.111:6443";
    private static final String CLUSTER_ADMIN_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg";
    private static final String NAMESPACE = "cp-namespace";
    private static final String DEFAULT_NAMESPACE = "temp-namespace";
    private static final String DEFAULT_VAL = "default-value";
    private static final String ALL_NAMESPACES = "all";
    private static final String USER_ID = "cp-user";
    private static final String USER_AUTH_ID = "cp-user-auth";
    private static final String SERVICE_ACCOUNT_NAME = "paasta";
    private static final String CREATED = "10-10-10";
    private static final String ROLE = "paas-ta-container-platform-init-role";
    private static final String ADMIN_ROLE = "paas-ta-container-platform-admin-role";
    private static final String SECRET_NAME = "paasta-token-jqrx4";
    private static final String SECRET_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ";

    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final boolean isAdmin = true;
    private static final String isAdminString = "true";
    private static final String isNotAdmin = "false";

    private static final String USER_TYPE_AUTH_CLUSTER_ADMIN = "administrator";
    private static final String USER_TYPE_AUTH_USER = "user";
    private static final String USER_TYPE_AUTH_NONE = "manager";
    private static final List<String> IGNORE_NAMESPACE_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add("kubernetes-dashboard");
            add("kube-node-lease");
            add("kube-public");
            add("kube-system");
            add("default");
            add("temp-namespace");
        }
    });

    private static Users users = new Users();
    private static Users modifyUsers = new Users();
    private static UsersAdmin usersAdmin = new UsersAdmin();

    private static UsersList gResultUsersListModel = null;
    private static UsersList modifyUsersList = new UsersList();
    private static UsersListAdmin gResultUsersAdminListModel = null;

    private static UsersInNamespace usersInNamespace = new UsersInNamespace();

    private static HashMap gResultMap = null;
    private static HashMap gResultNamesMap = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailStatusModel = null;

    private static List<String> gIgnoreNamespaceList = null;

    private static Params gParams = null;
    private static UsersDetailsList gResultUsersDetailsListModel = null;
    private static UsersDetails gResultUsersDetailsModel = null;
    private static Secrets gResultSecrets = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Mock
    ResourceYamlService resourceYamlService;

    @Mock
    VaultService vaultService;

    @Mock
    ClustersService clustersService;

    @Mock
    ResultStatusService resultStatusService;

    @Mock
    UsersService usersServiceMock;

    @InjectMocks
    UsersService usersService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();
        gResultNamesMap = new HashMap();
        gResultNamesMap.put("users", Arrays.asList("test", "paasta"));

        users = UsersModel.getResultUserWithClusterInfo();
        users.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        users.setEmail(DEFAULT_VAL);
        users.setUserType(AUTH_CLUSTER_ADMIN);
        users.setCpNamespace(NAMESPACE);
        users.setUserAuthId(USER_AUTH_ID);

        List<NamespaceRole> namespaceRoles = new ArrayList<>();
        NamespaceRole namespaceRole = new NamespaceRole();
        namespaceRole.setNamespace("test");
        namespaceRole.setRole("role");
        namespaceRoles.add(namespaceRole);
        users.setSelectValues(namespaceRoles);

        modifyUsers = UsersModel.getResultModifyUsersList();

        gResultUsersListModel = UsersModel.getResultUsersList();
        users.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        modifyUsersList = UsersModel.getResultUsersList();
        users.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultStatusModel = new ResultStatus();
        gResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gResultFailStatusModel = new ResultStatus();
        gResultFailStatusModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailStatusModel.setResultMessage(Constants.RESULT_STATUS_FAIL);

        gIgnoreNamespaceList = new ArrayList<String>();
        gIgnoreNamespaceList.add("default");
        gIgnoreNamespaceList.add("kubernetes-dashboard");
        gIgnoreNamespaceList.add("kube-node-lease");
        gIgnoreNamespaceList.add("kube-public");
        gIgnoreNamespaceList.add("kube-system");
        gIgnoreNamespaceList.add("paas-ta-container-platform-temp-namespace");
        gIgnoreNamespaceList.add("temp-namespace");

        UsersListAdmin.UserDetail userDetail= new UsersListAdmin.UserDetail();
        userDetail.setUserType(AUTH_CLUSTER_ADMIN);
        List<UsersListAdmin.UserDetail> userDetailsList = new ArrayList<>();
        userDetailsList.add(userDetail);

        usersInNamespace.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        usersInNamespace.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        usersInNamespace.setHttpStatusCode(CommonStatusCode.OK.getCode());
        usersInNamespace.setDetailMessage(CommonStatusCode.OK.getMsg());

        usersAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        usersAdmin.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        usersAdmin.setServiceAccountName(USER_ID);

        List<UsersAdmin.UsersDetails> usersDetailsList = new ArrayList<>();
        UsersAdmin.UsersDetails usersDetails = new UsersAdmin.UsersDetails();
        usersDetails.setCpNamespace(NAMESPACE);
        usersDetailsList.add(usersDetails);
        usersAdmin.setItems(usersDetailsList);

        gParams = new Params();
        gParams.setIsActive(CHECK_TRUE);
        gParams.setUserType(AUTH_CLUSTER_ADMIN);
        gParams.setCluster(CLUSTER);
        gParams.setUserAuthId(USER_AUTH_ID);

        gResultUsersDetailsListModel = new UsersDetailsList();
        gResultUsersDetailsModel = new UsersDetails();
        List<Users> usersList = new ArrayList<>();
        usersList.add(users);
        gResultUsersDetailsModel.setItems(usersList);
        gResultUsersDetailsModel.setUserType(AUTH_CLUSTER_ADMIN);


        gResultSecrets = new Secrets();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("ttt");
        commonMetaData.setUid("uid");
        commonMetaData.setCreationTimestamp("timestamp");
        gResultSecrets.setMetadata(commonMetaData);

        gResultUsersAdminListModel = new UsersListAdmin();

    }


    @Test
    public void getUsersAllByCluster() {
        when(restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_USERS_LIST_BY_CLUSTER
                        .replace("{cluster:.+}", gParams.getCluster())
                        .replace("{namespace:.+}", gParams.getNamespace()) + "?searchName=" + gParams.getSearchName().trim(), HttpMethod.GET,
                null, UsersDetailsList.class, gParams)).thenReturn(gResultUsersDetailsListModel);
        when(commonService.userListProcessing(gResultUsersDetailsListModel, gParams, UsersDetailsList.class)).thenReturn(gResultUsersDetailsListModel);
        when(commonService.setResultModel(gResultUsersDetailsListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultUsersDetailsListModel);

        usersService.getUsersAllByCluster(gParams);
    }

    @Test
    public void getUsersDetailsByCluster() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{userAuthId:.+}", gParams.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, gParams)).thenReturn(gResultUsersDetailsModel);
        when(propertyService.getClusterAdminNamespace()).thenReturn(NAMESPACE);
        when(resourceYamlService.getSecret(new Params(gResultUsersDetailsModel.getItems().get(0).getClusterId(), gResultUsersDetailsModel.getItems().get(0).getCpNamespace(), gResultUsersDetailsModel.getItems().get(0).getSaSecret(), true))).thenReturn(gResultSecrets);
        when(commonService.setResultModel(gResultUsersDetailsModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultUsersDetailsModel);

        usersService.getUsersDetailsByCluster(gParams);
    }

    @Test
    public void getUsersAccessInfo() {
        when(commonService.getClusterAuthorityFromContext(gParams.getCluster())).thenReturn(AUTH_CLUSTER_ADMIN);
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_INFO_USER_DETAILS//roleSetCode, clusterName
                .replace("{userAuthId:.+}", gParams.getUserAuthId())
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{userType:.+}", gParams.getUserType())
                .replace("{namespace:.+}", gParams.getNamespace()), HttpMethod.GET, null, Users.class, gParams)).thenReturn(users);
        when(commonService.getUserNameFromContext()).thenReturn(USER_AUTH_ID);
        when(commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS)).thenReturn(users);

        try {
            usersService.getUsersAccessInfo(gParams);
        } catch (Exception e) {
        }
    }

    @Test
    public void modifyToUser() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{userAuthId:.+}", gParams.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, gParams)).thenReturn(gResultUsersDetailsModel);
        when(commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

        try {
            usersService.modifyToUser(gParams, users);
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }

    @Test
    public void findUsers() {
        List<Users> usersList = new ArrayList<>();
        Users users = new Users();
        users.setCpNamespace(NAMESPACE);
        usersList.add(users);
        usersService.findUsers(usersList, NAMESPACE);
    }

    @Test
    public void findUsers_INVALID() {
        List<Users> usersList = new ArrayList<>();
        Users users = new Users();
        users.setCpNamespace("test");
        usersList.add(users);
        usersService.findUsers(usersList, NAMESPACE);
    }

    @Test
    public void getMappingClustersListByUser() {
        gParams.setIsGlobal(true);
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_LIST_BY_USER
                        .replace("{userAuthId:.+}", gParams.getUserAuthId()).replace("{userType:.+}", gParams.getUserType()),
                HttpMethod.GET, null, UsersList.class, gParams)).thenReturn(gResultUsersListModel);
        when(commonService.setResultModel(gResultUsersListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultUsersListModel);

        usersService.getMappingClustersListByUser(gParams);
    }

    @Test
    public void getMappingClustersAndNamespacesListByUser() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_AND_NAMESPACE_LIST_BY_USER
                        .replace("{userAuthId:.+}", gParams.getUserAuthId()).replace("{userType:.+}", gParams.getUserType()),
                HttpMethod.GET, null, UsersList.class, gParams)).thenReturn(gResultUsersListModel);
        when(commonService.setResultModel(gResultUsersListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultUsersListModel);
        usersService.getMappingClustersAndNamespacesListByUser(gParams);
    }

    @Test
    public void getMappingNamespacesListByUser() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_USER_AUTH_ID
                        .replace("{cluster:.+}", gParams.getCluster())
                        .replace("{userAuthId:.+}", gParams.getUserAuthId()),
                HttpMethod.GET, null, UsersList.class, gParams)).thenReturn(gResultUsersListModel);
        when(commonService.setResultModel(gResultUsersListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultUsersListModel);
        usersService.getMappingNamespacesListByUser(gParams);

    }

    @Test
    public void modifyToClusterAdmin() {
        gParams.setUserType(AUTH_USER);
        users.setUserType(AUTH_USER);
        gResultUsersDetailsModel.setUserType(AUTH_USER);
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{userAuthId:.+}", gParams.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, gParams)).thenReturn(gResultUsersDetailsModel);
        when(commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

        usersService.modifyToClusterAdmin(gParams, users);
    }

    @Test
    public void getClusterAdminListByCluster() {
        when(restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_CLUSTER_ADMIN_LIST
                        .replace("{cluster:.+}", gParams.getCluster())
                        .replace("{searchName:.+}", gParams.getSearchName().trim()), HttpMethod.GET, null, UsersListAdmin.class, gParams)).thenReturn(gResultUsersAdminListModel);
        when(commonService.userListProcessing(gResultUsersAdminListModel, gParams, UsersListAdmin.class)).thenReturn(gResultUsersAdminListModel);

        usersService.getClusterAdminListByCluster(gParams);
    }

    @Test
    public void deleteUsers() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_DELETE_USER
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{namespace:.+}", gParams.getNamespace())
                .replace("{userAuthId:.+}", gParams.getUserAuthId())
                .replace("{userType:.+}", gParams.getUserType()), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        usersService.deleteUsers(gParams);
    }

    @Test
    public void getUsersDetailByCluster() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_USER_DETAILS
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{userAuthId:.+}", gParams.getUserAuthId()), HttpMethod.GET, null, UsersDetails.class, gParams)).thenReturn(gResultUsersDetailsModel);

        usersService.getUsersDetailByCluster(gParams);
    }

    @Test
    public void deleteNamespaceAllUsers() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{namespace:.+}", gParams.getNamespace()), HttpMethod.GET, null, UsersList.class, gParams)).thenReturn(gResultUsersListModel);
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{namespace:.+}", gParams.getNamespace()), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        usersService.deleteNamespaceAllUsers(gParams);

    }

    @Test
    public void getAllUsersListByClusterAndNamespaces() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", gParams.getCluster())
                .replace("{namespace:.+}", gParams.getNamespace()), HttpMethod.GET, null, UsersList.class, gParams)).thenReturn(gResultUsersListModel);
        usersService.getAllUsersListByClusterAndNamespaces(gParams);
    }
}
