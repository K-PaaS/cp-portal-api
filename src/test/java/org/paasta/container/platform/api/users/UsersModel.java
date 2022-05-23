package org.paasta.container.platform.api.users;

import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.secret.Secrets;
import org.paasta.container.platform.api.users.Users.NamespaceRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Users Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.15
 **/

public class UsersModel {
    private static final String NAMESPACE = "cp-namespace";
    private static final String CREATION_TIME = "2020-11-17T09:31:37Z";

    public static UsersList getResultUsersList() {
        UsersList usersList = new UsersList();
        List<Users> users = new ArrayList<>();
        users.add(getResultUser());

        usersList.setItems(users);
        return usersList;
    }

    public static UsersList getResultUsersListWithClusterInfo() {
        UsersList usersList = new UsersList();
        List<Users> users = new ArrayList<>();
        users.add(getResultUserWithClusterInfo());
        users.add(getResultUserWithClusterInfoInTempNs());

        usersList.setItems(users);
        return usersList;
    }

    public static Users getResultUser() {
        Users users = new Users();
        users.setId(0);
        users.setUserId("paasta");
        users.setServiceAccountName("paasta");
        users.setRoleSetCode("paas-ta-container-platform-init-role");
        users.setIsActive("Y");
        users.setDescription("aaaa");
        users.setEmail("paasta@gmail.com");
        users.setPassword("paasta");
        users.setCpNamespace("cp-namespace");
        users.setSaSecret("paasta-token-jqrx4");
        users.setSaToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ");
        users.setUserType("CLUSTER_ADMIN");
        users.setCreated("2020-10-13");
        users.setLastModified("2020-10-13");
        users.setSelectValues(getResultNsRoleListModel());

        return users;
    }

    public static Users getResultModifyUsersList() {
        Users users = new Users();
        users.setId(0);
        users.setUserId("paasta");
        users.setServiceAccountName("paasta");
        users.setRoleSetCode("paas-ta-container-platform-admin-role");
        users.setIsActive("Y");
        users.setDescription("aaaa");
        users.setEmail("paasta@gmail.com");
        users.setPassword("paasta");
        users.setCpNamespace("cp-namespace");
        users.setSaSecret("paasta-token-jqrx4");
        users.setSaToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ");
        users.setUserType("CLUSTER_ADMIN");
        users.setCreated("2020-10-13");
        users.setLastModified("2020-10-13");
        users.setSelectValues(getResultNsRoleListModel());

        return users;
    }

    public static Users getResultUserWithClusterInfoInTempNs() {
        Users users = new Users();
        users.setId(0);
        users.setUserId("paasta");
        users.setServiceAccountName("paasta");
        users.setRoleSetCode("NOT_ASSIGNED_ROLE");
        users.setIsActive("Y");
        users.setDescription("aaaa");
        users.setEmail("paasta@gmail.com");
        users.setPassword("paasta");
        users.setCpNamespace("temp-namespace");
        users.setSaSecret("paasta-token-jqrx4");
        users.setSaToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ");
        users.setUserType("USER");
        users.setCreated("2020-10-13");
        users.setLastModified("2020-10-13");
        users.setSelectValues(getResultNsRoleListModel());
        users.setClusterApiUrl("111.111.111.111:6443");
        users.setClusterToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg");
        users.setClusterName("cp-cluster");

        return users;
    }

    public static Users getResultUserWithClusterInfo() {
        Users users = new Users();
        users.setId(0);
        users.setUserId("paasta");
        users.setServiceAccountName("paasta");
        users.setRoleSetCode("paas-ta-container-platform-init-role");
        users.setIsActive("Y");
        users.setDescription("aaaa");
        users.setEmail("paasta@gmail.com");
        users.setPassword("paasta");
        users.setCpNamespace("cp-namespace");
        users.setSaSecret("paasta-token-jqrx4");
        users.setSaToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ");
        users.setUserType("CLUSTER_ADMIN");
        users.setCreated("2020-10-13");
        users.setLastModified("2020-10-13");
        users.setSelectValues(getResultNsRoleListModel());
        users.setClusterApiUrl("111.111.111.111:6443");
        users.setClusterToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg");
        users.setClusterName("cp-cluster");

        return users;
    }

    public static List<NamespaceRole> getResultNsRoleListModel() {
        NamespaceRole namespaceRole = new NamespaceRole();
        namespaceRole.setNamespace("cp-namespace");
        namespaceRole.setRole("paas-ta-container-platform-init-role");

        List<NamespaceRole> nsRoleList = new ArrayList<>();
        nsRoleList.add(namespaceRole);

        return nsRoleList;
    }

    public static UsersListAdmin getResultUsersAdminList() {
        UsersListAdmin usersListAdmin = new UsersListAdmin();
        List<UsersListAdmin.UserDetail> details = new ArrayList<>();
        details.add(getResultUserDetailModel());

        CommonItemMetaData commonItemMetaData = new CommonItemMetaData();
        commonItemMetaData.setAllItemCount(9);
        commonItemMetaData.setRemainingItemCount(8);

        usersListAdmin.setItemMetaData(commonItemMetaData);
        usersListAdmin.setItems(details);

        return usersListAdmin;
    }

    public static UsersListAdmin getFinalResultUsersAdminList() {
        UsersListAdmin usersListAdmin = new UsersListAdmin();
        usersListAdmin = getResultUsersAdminList();
        usersListAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        usersListAdmin.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        usersListAdmin.setHttpStatusCode(CommonStatusCode.OK.getCode());
        usersListAdmin.setDetailMessage(CommonStatusCode.OK.getMsg());

        return usersListAdmin;
    }

    public static UsersListAdmin.UserDetail getResultUserDetailModel(){
        UsersListAdmin.UserDetail userDetail = new UsersListAdmin.UserDetail();
        userDetail.setUserId("paasta");
        userDetail.setServiceAccountName("paasta");
        userDetail.setCpNamespace("cp-namespace");
        userDetail.setRoleSetCode("paas-ta-container-platform-init-role");
        userDetail.setUserType("CLUSTER_ADMIN");
        userDetail.setCreated("2020-11-13");
        return userDetail;
    }

    public static UsersAdmin.Secrets getResultUserSecretModel(){
        UsersAdmin.Secrets secrets = new UsersAdmin.Secrets.SecretsBuilder()
                .saSecret("paasta-token-jqrx4")
                .secretLabels("paasta-label")
                .secretType("secret")
                .build();

        return secrets;
    }

    public static Secrets getSecrets() {
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("paasta-token-jqrx4");
        metaData.setLabels(new HashMap<>());

        Secrets secrets = new Secrets();
        secrets.setType("secret");
        secrets.setMetadata(metaData);

        return secrets;
    }

    public static Secrets getFinalSecrets() {
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("paasta-token-jqrx4");
        Map label = new HashMap();
        label.put("secretLabels", "paasta-label");
        metaData.setLabels(label);

        Secrets secrets = new Secrets();
        secrets.setType("secret");
        secrets.setMetadata(metaData);
        secrets.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        secrets.setResultMessage(Constants.RESULT_STATUS_SUCCESS);

        return secrets;
    }

    public static Map getResultUserSecretModelMap(){
        Map map = new HashMap();
        map.put("saSecret", "paasta-token-jqrx4");
        map.put("secretLabels", "paasta-label");
        map.put("secretType", "secret");

        return map;
    }

    public static UsersAdmin.UsersDetails getUsersDetails(){
        UsersAdmin.UsersDetails userDetail = new UsersAdmin.UsersDetails();
        userDetail.setCpNamespace("cp-namespace");
        userDetail.setRoleSetCode("paas-ta-container-platform-init-role");
        userDetail.setSecrets(getResultUserSecretModel());
        userDetail.setSaSecret("paasta-token-jqrx4");

        return userDetail;
    }

    public static List<UsersAdmin.UsersDetails> getUsersDetailsList(){
        List<UsersAdmin.UsersDetails> userDetailList = new ArrayList<>();
        userDetailList.add(getUsersDetails());

        return userDetailList;
    }

    public static UsersAdmin getUsersAdminList(){
        UsersAdmin usersAdmin = new UsersAdmin();

        usersAdmin.setUserId("1");
        usersAdmin.setServiceAccountName("paasta");
        usersAdmin.setCreated(CREATION_TIME);
        usersAdmin.setClusterName("cp-cluster");
        usersAdmin.setClusterApiUrl("111.111.111.111:6443");
        usersAdmin.setClusterToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg");
        usersAdmin.setItems(getUsersDetailsList());

        return usersAdmin;
    }
}
