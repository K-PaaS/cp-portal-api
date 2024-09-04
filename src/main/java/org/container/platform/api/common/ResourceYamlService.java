package org.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.container.platform.api.accessInfo.AccessToken;
import org.container.platform.api.clusters.limitRanges.LimitRangesDefault;
import org.container.platform.api.clusters.limitRanges.LimitRangesDefaultList;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefault;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefaultList;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.container.platform.api.common.model.CommonStatusCode;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.exception.ResultStatusException;
import org.container.platform.api.secret.Secrets;
import org.container.platform.api.secret.TokenRequest;
import org.container.platform.api.users.Users;
import org.container.platform.api.users.UsersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;

import static org.container.platform.api.common.Constants.*;


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

       try {
           restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                   propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
                   HttpMethod.DELETE, null, ResultStatus.class, params);
       }
       catch (Exception e) {
           if (Integer.valueOf(e.getMessage()) == CommonStatusCode.NOT_FOUND.getCode()) {
               LOGGER.info("*** EXCEPTION FOR DELETE SERVICE ACCOUNT : NOT FOUND...");
           } else {
               throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
           }
       }


       try {
           restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                   propertyService.getCpMasterApiListRoleBindingsDeleteUrl()
                           .replace("{namespace}", params.getNamespace())
                           .replace("{name}", params.getRs_sa() + Constants.NULL_REPLACE_TEXT + params.getRs_role() + "-binding"),
                   HttpMethod.DELETE, null, ResultStatus.class, params);
       }
       catch (Exception e) {
           if (Integer.valueOf(e.getMessage()) == CommonStatusCode.NOT_FOUND.getCode()) {
               LOGGER.info("*** EXCEPTION FOR DELETE ROLE BINDING : NOT FOUND...");
           } else {
               throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
           }
       }
    }


    /**
     * service account 의 secret 이름을 조회(Get Secret of Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public String getSecretName(Params params) {
        String jsonObj = "";
        try {
            jsonObj = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListUsersGetUrl().replace("{namespace}", params.getNamespace())
                            .replace("{name}", params.getRs_sa()), HttpMethod.GET, null, String.class, params);

        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED WHILE GET SECRET NAME ...");
        }

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
        HashMap responseMap = null;
        try {
            responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListSecretsGetUrl()
                            .replace("{namespace}", params.getNamespace())
                            .replace("{name}", params.getSaSecret()), HttpMethod.GET, null, Map.class, params);
        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED WHILE GET SECRET ...");
        }
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
        ResultStatus resultStatus = new ResultStatus();

        try {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getRs_sa()),
                    HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.NOT_FOUND.getCode()) {
                LOGGER.info("*** EXCEPTION FOR DELETE SERVICE ACCOUNT : NOT FOUND...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        return resultStatus;
    }


    /**
     * ClusterRole Binding 삭제(Delete ClusterRole Binding)
     *
     * @param params the params
     * @return
     */
    public ResultStatus deleteClusterRoleBinding(Params params) {
        ResultStatus resultStatus = new ResultStatus();

        try {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListClusterRoleBindingsDeleteUrl()
                            .replace("{name}", params.getRs_sa() + Constants.CLUSTER_ROLE_BINDING_NAME),
                    HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.NOT_FOUND.getCode()) {
                LOGGER.info("*** EXCEPTION FOR DELETE CLUSTER ROLE BINDING : NOT FOUND...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        return resultStatus;
    }


    public void createUserResource(Params params, Users users) {
        params.setIsClusterToken(true);
        params.setRs_sa(users.getServiceAccountName());

        try {
            // service-account  생성
            createServiceAccount(params);
        } catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.CONFLICT.getCode()) {
                LOGGER.info("*** CREATE_USER_RESOURCE: SERVICE ACCOUNT ALREADY EXISTS WITH THAT NAME...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        try {
            // role-binding 생성
            createRoleBinding(params);
        } catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.CONFLICT.getCode()) {
                LOGGER.info("*** CREATE_USER_RESOURCE: ROLE BINDING ALREADY EXISTS WITH THAT NAME...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        try {
            // token 생성
            createToken(params);
        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        // vault-user-token 등록
        params.setUserType(AUTH_USER);
        vaultService.saveUserAccessToken(params);

        Users newUser = new Users(params.getCluster(), params.getNamespace(), users.getUserId(), users.getUserAuthId(),
                AUTH_USER, params.getRs_role(), users.getServiceAccountName(), params.getSaSecret());

        // db 사용자 정보 등록
        createUsers(newUser);
    }


    public void createClusterAdminResource(Params params, Users users) {
        params.setIsClusterToken(true);
        params.setRs_sa(users.getServiceAccountName());
        params.setNamespace(propertyService.getClusterAdminNamespace());

        try {
            // service-account 생성
            createServiceAccount(params);
        } catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.CONFLICT.getCode()) {
                LOGGER.info("*** CREATE_CLUSTER_ADMIN_RESOURCE: SERVICE ACCOUNT ALREADY EXISTS WITH THAT NAME...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        try {
            // cluster-role-binding 생성
            createClusterRoleBinding(params);
        } catch (Exception e) {
            if (Integer.valueOf(e.getMessage()) == CommonStatusCode.CONFLICT.getCode()) {
                LOGGER.info("*** CREATE_CLUSTER_ADMIN_RESOURCE: CLUSTER ROLE BINDING ALREADY EXISTS WITH THAT NAME...");
            } else {
                throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
            }
        }

        try {
            // token 생성
            createToken(params);
        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }

        // vault-user-token 등록
        params.setUserType(AUTH_CLUSTER_ADMIN);
        vaultService.saveUserAccessToken(params);

        Users newClusterAdmin = new Users(params.getCluster(), propertyService.getDefaultNamespace(), users.getUserId(), users.getUserAuthId(),
                AUTH_CLUSTER_ADMIN, DEFAULT_CLUSTER_ADMIN_ROLE, users.getServiceAccountName(), params.getSaSecret());

        // db 사용자 정보 등록
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
        for (Users users : usersList.getItems()) {
            Params params = new Params(users.getClusterId(), users.getCpNamespace(), users.getUserAuthId(), users.getUserType(),
                    users.getServiceAccountName(), users.getRoleSetCode(), true);
            try {
                // delete sa and rb
                if (users.getUserType().equalsIgnoreCase(AUTH_CLUSTER_ADMIN)) {
                    params.setNamespace(propertyService.getClusterAdminNamespace());
                    deleteServiceAccount(params);
                    deleteClusterRoleBinding(params);
                } else {
                    deleteSaAndRb(params);
                }

                // delete cluster token in vault
                vaultService.deleteUserAccessToken(params);
            } catch (Exception e) {
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

    /**
     * ftl 파일로 Secret Service Account Token 생성(Create Secret Service Account Token)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createSecretServiceAccountToken(Params params) {
        Map map = new HashMap();

        params.setSaSecret(Constants.SA_TOKEN_NAME.replace("{username}", params.getRs_sa()));

        map.put("userName", params.getRs_sa());
        map.put("tokenName", params.getSaSecret());
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_secret_service_account_token.ftl", map));

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 Secret 생성(Create Secret)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public ResultStatus createSecrets(Params params) {
        Map map = new HashMap();
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        String encodedValue = "";

        map.put("metadataName", params.getMetadataName());
        map.put("namespace", params.getNamespace());
        map.put("dataType", params.getDataType());
        stringBuilder.append(templateService.convert("create_secret.ftl", map));
        stringBuilder.append(Constants.NEW_LINE);

        Map<String, Map> mapData = objectMapper.convertValue(params.getData(), Map.class);

        if (params.getDataType().equals(Constants.DATA_TYPE_SERVICE_ACCOUNT_TOKEN)) {
            map.put("tokenName", params.getMetadataName());
            map.put("spaceName", params.getNamespace());
            map.put("userName", params.getServiceAccountName());
            params.setYaml(templateService.convert("create_secret_service_account_token.ftl", map));
        } else if (params.getDataType().equals(Constants.DATA_TYPE_DOCKER_CFG) || params.getDataType().equals(Constants.DATA_TYPE_DOCKER_CONFIG_JSON)) {
            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                if (value.contains(DOCKER_CONFIG_AUTHS) && value.contains(DOCKER_CONFIG_AUTH)) {
                    encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                } else {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg());
                }

                if (isJSONValid(value)) {

                    if (key.contains(".")) {
                        line = "  " + key + ": |";
                    } else {
                        line = "  ." + key + ": |";
                    }

                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);
                    line = "    " + encodedValue;
                    stringBuilder.append(line);
                } else {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg());
                }
            }
        } else if (params.getDataType().equals(Constants.DATA_TYPE_SSH_AUTH) || params.getDataType().equals(Constants.DATA_TYPE_TLS)) {

            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                if (key.equals(TLS_KEY) || key.equals(SSH_PRIVATE_KEY)) {
                    if (value.contains(PEM_HEADER) && value.contains(PEM_FOOTER) && value.contains(DATA_LABEL_PRIVATE_KEY)) {
                        encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                    } else {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_PRIVATE_KEY_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_PRIVATE_KEY_FILE.getMsg());
                    }
                } else if (key.equals(TLS_CRT)) {
                    if (value.contains(PEM_HEADER) && value.contains(PEM_FOOTER) && value.contains(DATA_LABEL_CERTIFICATE)) {
                        encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                    } else {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_CERTIFICATE_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_CERTIFICATE_FILE.getMsg());
                    }
                }

                line = "  " + key + ": |";
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);

                line = "    " + encodedValue;
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }

        } else if (params.getDataType().equals(Constants.DATA_TYPE_BOOTSTRAP_TOKEN_DATA) || params.getDataType().equals(Constants.DATA_TYPE_OPAQUE)) {

            for (Map.Entry<String, Map> entry : mapData.entrySet()) {

                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                encodedValue = Base64.getEncoder().encodeToString(value.getBytes());

                line = "  " + key + ": " + encodedValue;
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }

        }

        params.setYaml(String.valueOf(stringBuilder));
        return null;
    }

    /**
     * Secret 수정(Update Secret)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public ResultStatus updateSecrets(Params params, org.container.platform.api.secrets.Secrets secrets) {
        String line = "";
        Map map = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map> mapData = objectMapper.convertValue(params.getData(), Map.class);
        String encodedValue = "";
        String decodedValue = "";

        map.put("metadataName", params.getMetadataName());
        map.put("namespace", params.getNamespace());
        map.put("dataType", params.getDataType());
        stringBuilder.append(templateService.convert("create_secret.ftl", map));
        stringBuilder.append(Constants.NEW_LINE);

        if (params.getDataType().equals(Constants.DATA_TYPE_SERVICE_ACCOUNT_TOKEN)) {

        } else if (params.getDataType().equals(Constants.DATA_TYPE_DOCKER_CFG) || params.getDataType().equals(Constants.DATA_TYPE_DOCKER_CONFIG_JSON)) {
            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                if (value.contains(DOCKER_CONFIG_AUTHS) && value.contains(DOCKER_CONFIG_AUTH)) {
                    encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                } else {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg());
                }

                if (value.isEmpty()) {
                    for (Map.Entry<String, String> OriginEntry: secrets.getData().entrySet()) {
                        String OriginKey = OriginEntry.getKey();
                        String OriginValue = OriginEntry.getKey();

                        if (OriginKey.equals(key)) {
                            value = OriginValue;
                        }
                    }
                }

                if (isJSONValid(value)) {

                    if (key.contains(".")) {
                        line = "  " + key + ": |";
                    } else {
                        line = "  ." + key + ": |";
                    }

                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);
                    line = "    " + encodedValue;
                    stringBuilder.append(line);
                } else {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_DOCKER_CONFIG_FILE.getMsg());
                }
            }
        } else if (params.getDataType().equals(Constants.DATA_TYPE_SSH_AUTH) || params.getDataType().equals(Constants.DATA_TYPE_TLS)) {
            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                if (value.isEmpty()) {
                    for (Map.Entry<String, String> OriginEntry: secrets.getData().entrySet()) {
                        String OriginKey = OriginEntry.getKey();
                        String OriginValue = OriginEntry.getValue();

                        if (OriginKey.equals(key)) {
                            value = OriginValue;
                            byte[] decodedByte = Base64.getDecoder().decode(value);
                            decodedValue = new String(decodedByte);
                            value = decodedValue;
                        }
                    }

                } else {
                    if (value.contains(TAG_BR)) {
                        value = value.replaceAll(TAG_BR, "\r\n");
                    } else if (value.contains(TAG_DIV_END)) {
                        value = value.replaceAll(TAG_DIV_END, "\r\n");
                        value = value.replaceAll(TAG_DIV_BEGIN, "");
                    }
                }

                if (key.equals(TLS_KEY) || key.equals(SSH_PRIVATE_KEY)) {
                    if (value.contains(PEM_HEADER) && value.contains(PEM_FOOTER) && value.contains(DATA_LABEL_PRIVATE_KEY)) {
                        encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                    } else {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_PRIVATE_KEY_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_PRIVATE_KEY_FILE.getMsg());
                    }
                } else if (key.equals(TLS_CRT)) {
                    if (value.contains(PEM_HEADER) && value.contains(PEM_FOOTER) && value.contains(DATA_LABEL_CERTIFICATE)) {
                        encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
                    } else {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_CERTIFICATE_FILE.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_CERTIFICATE_FILE.getMsg());
                    }
                }

                line = "  " + key + ": |";
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);

                line = "    " + encodedValue;
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);

            }

        } else if (params.getDataType().equals(Constants.DATA_TYPE_BOOTSTRAP_TOKEN_DATA) || params.getDataType().equals(Constants.DATA_TYPE_OPAQUE)) {
            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();
                String key = data.get(MAP_KEY).toString();
                String value = data.get(MAP_VALUE).toString();

                if (value.isEmpty()) {
                    for (Map.Entry<String, String> OriginEntry : secrets.getData().entrySet()) {
                        String OriginKey = OriginEntry.getKey();
                        String OriginValue = OriginEntry.getKey();

                        if (OriginKey.equals(key)) {
                            value = OriginValue;
                        }
                    }

                }

                encodedValue = Base64.getEncoder().encodeToString(value.getBytes());

                line = "  " + key + ": " + encodedValue;
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }

        }

        params.setYaml(String.valueOf(stringBuilder));
        return null;
    }

    /**
     * json 파일로 Token 생성(Create Token)
     *
     * @param params the params
     * @return the HashMap
     */
    public void createToken(Params params) {
        String bodyObject = templateService.get("create_token.json");
        HashMap responseMap =  (HashMap) restTemplateService.sendGlobal(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListTokensCreateUrl()
                        .replace("{namespace}", params.getNamespace())
                        .replace("{name}",  params.getRs_sa()), HttpMethod.POST, bodyObject, Map.class, params);

        TokenRequest tokenRequest = commonService.setResultObject(responseMap, TokenRequest.class);
        params.setSaToken(tokenRequest.getToken());
    }

    /**
     * json 형태 확인(Check Json Type)
     *
     * @string jsonInString the string
     * @return the boolean
     */
    public boolean isJSONValid(String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }

    }

}