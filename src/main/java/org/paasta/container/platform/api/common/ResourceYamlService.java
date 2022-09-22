package org.paasta.container.platform.api.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.paasta.container.platform.api.accessInfo.AccessToken;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesDefault;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesDefaultList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefault;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefaultList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.secret.Secrets;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


import java.util.*;

import static org.paasta.container.platform.api.common.Constants.*;


/**
 * Resource Yaml Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.14
 **/
@Service
public class ResourceYamlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceYamlService.class);

    private final CommonService commonService;
    private final PropertyService propertyService;
    private final TemplateService templateService;
    private final RestTemplateService restTemplateService;
    private final ResourceQuotasService resourceQuotasService;
    private final VaultService vaultService;

    @Autowired
    public ResourceYamlService(CommonService commonService, PropertyService propertyService, TemplateService templateService, RestTemplateService restTemplateService,
                               ResourceQuotasService resourceQuotasService, VaultService vaultService) {
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.templateService = templateService;
        this.restTemplateService = restTemplateService;
        this.resourceQuotasService = resourceQuotasService;
        this.vaultService = vaultService;
    }


    /**
     * ftl 파일로 Namespace 생성(Create Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createNamespace(Params params) {
        Map map = new HashMap();
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_namespace.ftl", map));
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 Service Account 생성(Create Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createServiceAccount(Params params) {
        Map map = new HashMap();
        map.put("userName", params.getRs_sa());
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_account.ftl", map));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 Role Binding 생성(Create Role Binding)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createRoleBinding(Params params) {
        Map map = new HashMap();

        map.put("userName", params.getRs_sa());
        map.put("roleName", params.getRs_role());
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_roleBinding.ftl", map));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRoleBindingsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 init role 생성(Create init role)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createInitRole(Params params) {
        // init role 생성
        Map<String, Object> map = new HashMap();
        map.put("spaceName", params.getNamespace());
        map.put("roleName", propertyService.getInitRole());

        params.setYaml(templateService.convert("create_init_role.ftl", map));
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 admin role 생성(Create admin role)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createAdminRole(Params params) {
        Map<String, Object> map = new HashMap();
        map.put("spaceName", params.getNamespace());
        map.put("roleName", propertyService.getAdminRole());

        params.setYaml(templateService.convert("create_admin_role.ftl", map));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Namespace 에 ResourceQuotas 를 할당(Allocate ResourceQuotas to Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createDefaultResourceQuota(Params params) {
        ResourceQuotasDefaultList resourceQuotasDefaultList = restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class, params);
        String limitsCpu = "";
        String limitsMemory = "";

        String rqName = params.getRs_rq();

        for (ResourceQuotasDefault d : resourceQuotasDefaultList.getItems()) {
            if (rqName.equals(Constants.EMPTY_STRING)) {
                rqName = propertyService.getLowResourceQuotas();
            }

            if (propertyService.getResourceQuotasList().contains(rqName) && d.getName().equals(rqName)) {
                limitsCpu = d.getLimitCpu();
                limitsMemory = d.getLimitMemory();

                break;
            }
        }
        Map<String, Object> model = new HashMap<>();
        model.put("name", rqName);
        model.put("namespace", params.getNamespace());
        model.put("limits_cpu", limitsCpu);
        model.put("limits_memory", limitsMemory);

        params.setYaml(templateService.convert("create_resource_quota.ftl", model));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespace 에 LimitRanges 를 할당(Allocate LimitRanges to Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createDefaultLimitRanges(Params params) {
        LimitRangesDefaultList limitRangesDefaultList = restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class, params);
        String limitsCpu = "";
        String limitsMemory = "";

        String lrName = params.getRs_lr();

        for (LimitRangesDefault limitRanges : limitRangesDefaultList.getItems()) {
            if (lrName.equals(Constants.EMPTY_STRING)) {
                lrName = propertyService.getLowLimitRanges();
            }

            if (propertyService.getLimitRangesList().contains(lrName) && limitRanges.getName().equals(lrName)) {
                if (Constants.SUPPORTED_RESOURCE_CPU.equals(limitRanges.getResource())) {
                    limitsCpu = limitRanges.getDefaultLimit();
                } else {
                    limitsMemory = limitRanges.getDefaultLimit();
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("name", lrName);
        model.put("namespace", params.getNamespace());
        model.put("limit_cpu", limitsCpu);
        model.put("limit_memory", limitsMemory);

        params.setYaml(templateService.convert("create_limit_range.ftl", model));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ServiceAccount 와 RoleBinding 삭제 (Delete ServiceAccount and Role binding)
     *
     * @param params the params
     * @return
     */
    public void deleteSaAndRb(Params params) {

        restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
                HttpMethod.DELETE, null, ResultStatus.class, params);

        restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRoleBindingsDeleteUrl()
                        .replace("{namespace}", params.getNamespace())
                        .replace("{name}", params.getRs_sa() + Constants.NULL_REPLACE_TEXT + params.getRs_role() + "-binding"),
                HttpMethod.DELETE, null, ResultStatus.class, params);
    }



    /**
     * service account 의 secret 이름을 조회(Get Secret of Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public String getSecretName(Params params) {
        String jsonObj = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersGetUrl().replace("{namespace}", params.getNamespace())
                        .replace("{name}", params.getRs_sa()), HttpMethod.GET, null, String.class, params);

        JsonObject jsonObject = JsonParser.parseString(jsonObj).getAsJsonObject();
        JsonElement element = jsonObject.getAsJsonObject().get("secrets");
        element = element.getAsJsonArray().get(0);
        String secretName = element.getAsJsonObject().get("name").toString();
        secretName = secretName.replaceAll("\"", "");

        params.setSaSecret(secretName);
        return secretName;
    }


    /**
     * ftl 파일로 init role 생성(Create init role)
     *
     * @param params the params
     * @return the resultStatus
     */
    public void deleteRoleBinding(Params params) {
        restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRoleBindingsDeleteUrl()
                        .replace("{namespace}", params.getNamespace())
                        .replace("{name}", params.getRs_sa() + Constants.NULL_REPLACE_TEXT + params.getRs_role() + "-binding"),
                HttpMethod.DELETE, null, ResultStatus.class, params);
    }


    public AccessToken getSecrets(Params params) {
        String caCertToken;
        String userToken;

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl()
                        .replace("{namespace}", params.getNamespace())
                        .replace("{name}", params.getSaSecret()), HttpMethod.GET, null, Map.class, params);

        Map map = (Map) responseMap.get("data");

        caCertToken = map.get("ca.crt").toString();
        userToken = map.get("token").toString();

        Base64.Decoder decoder = Base64.getDecoder();
        String caCertDecodeToken = new String(decoder.decode(caCertToken));
        String userDecodeToken = new String(decoder.decode(userToken));

        AccessToken accessToken = new AccessToken();
        accessToken.setCaCertToken(caCertDecodeToken);
        accessToken.setUserAccessToken(userDecodeToken);

        params.setSaToken(userDecodeToken);
        return (AccessToken) commonService.setResultModel(commonService.setResultObject(accessToken, AccessToken.class), Constants.RESULT_STATUS_SUCCESS);
    }


    //////////////////////////////////////////////


    /**
     * ftl 파일로 ClusterRole Binding 생성(Create ClusterRole Binding)
     *
     * @param params the params
     * @return
     */
    public ResultStatus createClusterRoleBinding(Params params) {
        Map map = new HashMap();
        map.put("userName", params.getRs_sa());
        map.put("crbName", Constants.CLUSTER_ROLE_BINDING_NAME);
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_clusterRoleBinding.ftl", map));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListClusterRoleBindingsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secret 조회(Get Secret of Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public Secrets getSecret(Params params) {
        return restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListSecretsGetUrl()
                .replace("{namespace}", params.getNamespace())
                .replace("{name}", params.getResourceName()), HttpMethod.GET, null, Secrets.class, params);
    }




    /**
     * Service Account 삭제(Delete Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteServiceAccount(Params params) {
        return restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
                HttpMethod.DELETE, null, ResultStatus.class, params);
    }


    /**
     * ClusterRole Binding 삭제(Delete ClusterRole Binding)
     *
     * @param params the params
     * @return
     */
    public ResultStatus deleteClusterRoleBinding(Params params) {
        return restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListClusterRoleBindingsDeleteUrl()
                        .replace("{name}", params.getRs_sa() + Constants.CLUSTER_ROLE_BINDING_NAME),
                HttpMethod.DELETE, null, ResultStatus.class, params);
    }


    public void createUserResource(Params params, Users users) {
        params.setIsClusterToken(true);
        // service-account  생성
        params.setRs_sa(users.getServiceAccountName());
        createServiceAccount(params);
        // role-binding 생성
        createRoleBinding(params);
        getSecretName(params);
        getSecrets(params);

        // vault-user-token 등록
        params.setUserType(AUTH_USER);
        vaultService.saveUserAccessToken(params);

        Users newUser = new Users(params.getCluster(), params.getNamespace(), users.getUserId(), users.getUserAuthId(),
                AUTH_USER, params.getRs_role(), users.getServiceAccountName(), params.getSaSecret());

        createUsers(newUser);
    }


    public void createClusterAdminResource(Params params, Users users) {
        params.setIsClusterToken(true);
        params.setRs_sa(users.getServiceAccountName());
        params.setNamespace(propertyService.getClusterAdminNamespace());
        // service-account 생성
        createServiceAccount(params);
        // cluster-role-binding 생성
        createClusterRoleBinding(params);
        getSecretName(params);
        getSecrets(params);

        // vault user path에 token 저장
        params.setUserType(AUTH_CLUSTER_ADMIN);
        vaultService.saveUserAccessToken(params);

        Users newClusterAdmin = new Users(params.getCluster(), propertyService.getDefaultNamespace(), users.getUserId(), users.getUserAuthId(),
                AUTH_CLUSTER_ADMIN, DEFAULT_CLUSTER_ADMIN_ROLE, users.getServiceAccountName(), params.getSaSecret());

        createUsers(newClusterAdmin);
    }


    /**
     * Users Resource 삭제(Delete Users Resource)
     *
     * @param users the users
     * @return return is succeeded
     */

    public void deleteUserResource(Users users) {
        // SA and RoleBinding 삭제
        Params params = new Params(users.getClusterId(), users.getCpNamespace(), users.getUserAuthId(), AUTH_USER,
                users.getServiceAccountName(), users.getRoleSetCode(), true);
        // sa : replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
       // rb : replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa() + Constants.NULL_REPLACE_TEXT + params.getRs_role() + "-binding"),
        deleteSaAndRb(params);
        // vault Token 삭제
        vaultService.deleteUserAccessToken(params);
        // DB 삭제
        deleteUsers(params);
    }

    //////////////////////////////////////////////
    public void deleteClusterAdminResource(Users users) {
        Params params = new Params(users.getClusterId(), propertyService.getClusterAdminNamespace(), users.getUserAuthId(), AUTH_CLUSTER_ADMIN,
                users.getServiceAccountName(), DEFAULT_CLUSTER_ADMIN_ROLE, true);
        // SA and RoleBinding 삭제
        //.replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
        deleteServiceAccount(params);
        //.replace("{name}", params.getRs_sa() + Constants.CLUSTER_ROLE_BINDING_NAME)
        deleteClusterRoleBinding(params);
        // vault 내 access-token 삭제
        vaultService.deleteUserAccessToken(params);
        // db 삭제
        params.setNamespace(propertyService.getDefaultNamespace());
        deleteUsers(params);
    }


    public void deleteUserResourceForNonExistentUser(UsersList usersList) {
        for(Users users : usersList.getItems()) {
            Params params  = new Params(users.getClusterId(), users.getCpNamespace(), users.getUserAuthId(), users.getUserType(),
            users.getServiceAccountName(), users.getRoleSetCode(), true);
            try{
                // delete sa and rb
                if(users.getUserType().equalsIgnoreCase(AUTH_CLUSTER_ADMIN)) {
                    params.setNamespace(propertyService.getClusterAdminNamespace());
                    deleteServiceAccount(params);
                    deleteClusterRoleBinding(params);
                }
                else {
                    deleteSaAndRb(params);
                }

                // delete cluster token in vault
                vaultService.deleteUserAccessToken(params);
            }
            catch (Exception e ){
                LOGGER.info("***** EXCEPTION OCCURRED WHILE DELETE RESOURCES FROM NON-EXISTENT USER...");
            }

        }

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
     * Users 삭제(Delete Users)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deleteUsersById(Params params) {
        return restTemplateService.send(TARGET_COMMON_API,
                Constants.URI_COMMON_API_USER_DELETE + params.getId(), HttpMethod.DELETE, null, ResultStatus.class, params);
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


}