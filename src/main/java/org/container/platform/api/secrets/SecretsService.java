package org.container.platform.api.secrets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.secrets.vaultSecrets.*;
import org.container.platform.api.workloads.pods.Pods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.container.platform.api.common.Constants.*;


/**
 * Secrets Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/
@Service
public class SecretsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResourceYamlService resourceYamlService;
    private final TemplateService templateService;

    /**
     * Instantiates a new Secrets service
     *
     * @param restTemplateService  the rest template service
     * @param commonService        the common service
     * @param propertyService      the property service
     * @param resourceYamlService  the resource yaml service
     */
    @Autowired
    public SecretsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, ResourceYamlService resourceYamlService, TemplateService templateService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resourceYamlService = resourceYamlService;
        this.templateService = templateService;
    }

    /**
     * Secrets 목록 조회(Get Secrets List)
     *
     * @param params the params
     * @return the Secrets list
     */
    public SecretsList getSecretsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsListUrl(), HttpMethod.GET, null, Map.class, params);
        return procSecretsList(responseMap, params);
    }

    /**
     * Vault Secrets 목록 조회(Get Secrets List)
     *
     * @param params the params
     * @return the Secrets list
     */
    public DatabaseInfoList getVaultSecretsList(Params params) {
        DatabaseInfoList databaseInfoList = new DatabaseInfoList();

        List<Map<String,String>> list = null;
        list = new ArrayList<>();

        Map<String, String> map = null;

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretListUrl(), HttpMethod.GET, null, Map.class, params);
        VaultDynamicSecretsList vaultDynamicSecretsList = commonService.setResultObject(responseMap, VaultDynamicSecretsList.class);

        //DB에서 POD 조회
        //restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.GET, null, VaultDatabaseSecrets.class, params);
       /* for (int i=0; i < vaultDynamicSecretsList.getItems().size(); i++) {
            String path = vaultDynamicSecretsList.getItems().get(i).getSpec().getPath();
            int idx = path.indexOf("/");
            String name = path.substring(idx+1);
            int idx2 = name.indexOf(SUB_STRING_ROLE);
            name = name.substring(0, idx2);
            //map.put("name", name);


            VaultDatabaseSecrets getVDS = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                    .replace("{name:.+}", name), HttpMethod.GET, null, VaultDatabaseSecrets.class, params);

            params.setResourceName(getVDS.getAppName());

            if (getVDS.getAppName() != null) {
                //app name 으로 파드 조회
                HashMap responseMap2 = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);

                Pods pods = commonService.setResultObject(responseMap2, Pods.class);

                String status = pods.getPodStatus();
                map.put("applicableStatus", status);
            } else if (getVDS.getAppName() == null) {
                map.put("applicableStatus", "-");
            }
        }*/

        for (int i=0; i < vaultDynamicSecretsList.getItems().size(); i++) {
            map = new HashMap<>();
            String path = vaultDynamicSecretsList.getItems().get(i).getSpec().getPath();
            String createdTime = vaultDynamicSecretsList.getItems().get(i).getMetadata().getCreationTimestamp();
            map.put("createdTime", createdTime);

            int idx = path.indexOf("/");
            String name = path.substring(idx+1);
            int idx2 = name.indexOf(SUB_STRING_ROLE);
            name = name.substring(0, idx2);
            map.put("name", name);

            HashMap dbType = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", name),
                    HttpMethod.GET, null, Map.class, params);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map> mapData = objectMapper.convertValue(dbType, Map.class);

            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                if (entry.getKey().equals(DATA)) {
                    Map data = entry.getValue();

                    Map<String, String> mapData2 = objectMapper.convertValue(data, Map.class);
                    for (Map.Entry<String, String> entry2 : mapData2.entrySet()) {
                        if (entry2.getKey().equals(PLUGIN_NAME)) {
                            String pluginName = entry2.getValue();
                            if (pluginName.equals(POSTGRESQL_DATABASE_PLUGIN)) {
                                pluginName = POSTGRESQL_DATABASE;
                                map.put("pluginName", pluginName);
                            }
                        }
                    }
                }
            }

            VaultDatabaseSecrets getVDS = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                    .replace("{name:.+}", name), HttpMethod.GET, null, VaultDatabaseSecrets.class, params);

            params.setResourceName(getVDS.getAppName());
            params.setNamespace(getVDS.getAppNamespace());

            if (getVDS.getAppName() != null) {
                HashMap responseMap2 = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);

                Pods pods = commonService.setResultObject(responseMap2, Pods.class);

                String status = pods.getPodStatus();
                map.put("applicableStatus", status);
            } else {
                map.put("applicableStatus", STATUS_OFF);
            }

            list.add(map);
            databaseInfoList.setItems(list);
        }

        return (DatabaseInfoList) commonService.setResultModel(databaseInfoList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Secrets 상세 조회(Get Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    public Secrets getSecrets(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, params);
        Secrets secrets = commonService.setResultObject(responseMap, Secrets.class);
        secrets = commonService.annotationsProcessing(secrets, Secrets.class);

        return (Secrets) commonService.setResultModel(secrets, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Vault Secrets 상세 조회(Get Vault Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    public DatabaseCredentials getVaultSecrets(Params params) {

        HashMap tTl = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getResourceName()),
                HttpMethod.GET, null, Map.class, params);

        DatabaseRoles databaseRoles = commonService.setResultObject(tTl, DatabaseRoles.class);

        DatabaseCredentials databaseCredentials = restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseCredentialsPath().replace("{name}", params.getResourceName()),
                HttpMethod.GET, null, DatabaseCredentials.class, params);

        HashMap dbType = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getResourceName()),
                HttpMethod.GET, null, Map.class, params);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map> mapData = objectMapper.convertValue(dbType, Map.class);

        for (Map.Entry<String, Map> entry : mapData.entrySet()) {
            if (entry.getKey().equals(DATA)) {
                Map data = entry.getValue();

                Map<String, String> mapData2 = objectMapper.convertValue(data, Map.class);
                for (Map.Entry<String, String> entry2 : mapData2.entrySet()) {
                    if (entry2.getKey().equals(PLUGIN_NAME)) {
                        String pluginName = entry2.getValue();
                        if (pluginName.equals(POSTGRESQL_DATABASE_PLUGIN)) {
                            pluginName = POSTGRESQL_DATABASE;
                            databaseCredentials.setPlugin_name(pluginName);
                        }
                    }
                }
            }
        }

        databaseCredentials.setDefault_ttl(databaseRoles.getData().getDefault_ttl());
        databaseCredentials.setMax_ttl(databaseRoles.getData().getMax_ttl());

        //DB 상세 조회 후
        VaultDatabaseSecrets getVDS = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", params.getResourceName()), HttpMethod.GET, null, VaultDatabaseSecrets.class, params);

        databaseCredentials.setFlag(getVDS.getFlag());
        params.setResourceName(getVDS.getAppName());
        params.setNamespace(getVDS.getAppNamespace());

        //파드 이름으로 파드 조회
        if (getVDS.getAppName() != null) {
            databaseCredentials.setApplication(getVDS.getAppName());
            HashMap responseMap2 = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);

            Pods pods = commonService.setResultObject(responseMap2, Pods.class);

            String status = pods.getPodStatus();
            databaseCredentials.setStatus(status);
        }

        return (DatabaseCredentials) commonService.setResultModel(databaseCredentials, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secrets YAML 조회(Get Secrets Yaml)
     *
     * @param params the params
     * @return the Secrets yaml
     */
    public CommonResourcesYaml getSecretsYaml(Params params){
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * Secrets 생성(Create Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createSecrets(Params params) {

        ResultStatus resultStatus = new ResultStatus();
        StringBuilder stringBuilder = new StringBuilder();

        if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_KUBERNETES)) {
            resourceYamlService.createSecrets(params);

            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListSecretsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        } else if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_VAULT)) {

            Map map = new HashMap();
            String line = "";

            map.put("name", params.getMetadataName());
            map.put("defaultTtl", params.getDefaultTtl());
            map.put("maxTtl", params.getMaxTtl());
            map.put("serviceName", params.getDbService());

            HashMap auth = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE),
                    HttpMethod.GET, null, Map.class, params);

            VaultDatabaseSecrets vaultDatabaseSecrets = setVaultDatabaseSecrets(params);
            restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.POST, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

            AuthKubernetesRoles authKubernetesRoles = commonService.setResultObject(auth, AuthKubernetesRoles.class);
            stringBuilder.append(templateService.get("create_vault_access_authentication_method_role.ftl"));
            stringBuilder.append(Constants.NEW_LINE);
            line = "      " + "\"" + params.getMetadataName() + "-policy\"";
            stringBuilder.append(line);

            if (authKubernetesRoles.getData().getToken_policies().isEmpty()) {
                stringBuilder.append(Constants.NEW_LINE);
                line = "    ]";
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
                line = "}";
                stringBuilder.append(line);

            } else {
                line = ", ";
                stringBuilder.append(line);

                for (int i = 0; i < authKubernetesRoles.getData().getToken_policies().size(); i++) {
                    line = "\"" + authKubernetesRoles.getData().getToken_policies().get(i).toString() + "\"";
                    stringBuilder.append(line);
                    if (i != authKubernetesRoles.getData().getToken_policies().size() - 1) {
                        line = ", ";
                        stringBuilder.append(line);
                    }
                }
            }

            stringBuilder.append(Constants.NEW_LINE);
            line = "    ]";
            stringBuilder.append(line);
            stringBuilder.append(Constants.NEW_LINE);
            line = "}";
            stringBuilder.append(line);

            restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.POST, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_config.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_policy.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE),
                    HttpMethod.PUT, String.valueOf(stringBuilder), ResultStatus.class, params);

            params.setYaml(templateService.convert("create_vault_dynamic_secret.ftl", map));

            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getVaultVaultDynamicSecretCreateUrl(),HttpMethod.POST, ResultStatus.class, params);
        }

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Vault Secrets App 적용(Apply Vault Secrets for App)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus applyVaultSecrets(Params params) {
        StringBuilder stringBuilder = new StringBuilder();
        Map map = new HashMap();
        String line = "";

        params.setResourceName("jjy-5569b67d56-9pkj2");
        params.setDbService("test1");

        map.put("name", params.getDbService());
        map.put("namespace", params.getNamespace());

        String yamlHead = "";
        String yamlBody = "";

      /*  // service account 만들기
        String serviceAccountYaml = templateService.convert("create_vault_service_account.ftl", map);
        params.setYaml(serviceAccountYaml);

        restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        // 서비스 account policy 만들기


        // k8s 서비스 account role 생성
        HashMap auth = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", params.getResourceName()),
                HttpMethod.GET, null, Map.class, params);*/

        // 해당 pod yaml 조회
        /*String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);

        System.out.println(resourceYaml);

        int idx = resourceYaml.indexOf("  creationTimestamp:");
        yamlHead = resourceYaml.substring(0, idx);
        yamlBody = resourceYaml.substring(idx);

        // yaml 수정
        stringBuilder.append(yamlHead);
        stringBuilder.append(templateService.convert("create_vault_agent_inject_secret_annotation.ftl", map));
        stringBuilder.append(NEW_LINE);
        stringBuilder.append(yamlBody);

        params.setYaml(String.valueOf(stringBuilder));
        System.out.println(params.getYaml());*/

        //DB 수정 Logic Application 이름, namespace 인풋
        VaultDatabaseSecrets vaultDatabaseSecrets = new VaultDatabaseSecrets();
        vaultDatabaseSecrets.setName(params.getDbService());
        vaultDatabaseSecrets.setAppName(params.getResourceName());
        vaultDatabaseSecrets.setAppNamespace(params.getNamespace());
        vaultDatabaseSecrets.setFlag(CHECK_Y);

        restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.PUT, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

        //restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.PUT, setVaultDatabaseSecrets(params), VaultDatabaseSecrets.class, params);

        // yaml 생성
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Secrets 삭제(Delete Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteSecrets(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Vault Secrets 삭제(Delete Vault Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteVaultSecrets(Params params) {

        StringBuilder stringBuilder = new StringBuilder();
        String line = "";

        VaultDatabaseSecrets getVDS = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", params.getResourceName()), HttpMethod.GET, null, VaultDatabaseSecrets.class, params);

        if (getVDS.getAppName() != null && getVDS.getAppNamespace() != null) {
            params.setResourceName(getVDS.getAppName());
            params.setNamespace(getVDS.getAppNamespace());
            restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListPodsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }

        restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", params.getResourceName()), HttpMethod.DELETE, null, VaultDatabaseSecrets.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getResourceName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getResourceName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getResourceName()),
                HttpMethod.DELETE, null , Object.class, params);

        HashMap auth = (HashMap) restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE),
                HttpMethod.GET, null, Map.class, params);

        AuthKubernetesRoles authKubernetesRoles = commonService.setResultObject(auth, AuthKubernetesRoles.class);
        stringBuilder.append(templateService.get("create_vault_access_authentication_method_role.ftl"));
        stringBuilder.append(Constants.NEW_LINE);
        line = "      ";
        stringBuilder.append(line);

        for (int i=0; i < authKubernetesRoles.getData().getToken_policies().size(); i++) {
            String tokenPolicy = authKubernetesRoles.getData().getToken_policies().get(i).toString();
            if (!tokenPolicy.equals(params.getResourceName() + "-policy")) {
                line = "\"" + tokenPolicy + "\"";
                stringBuilder.append(line);
                line = ", ";
                stringBuilder.append(line);
            }
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-2);

        stringBuilder.append(Constants.NEW_LINE);
        line = "    ]";
        stringBuilder.append(line);
        stringBuilder.append(Constants.NEW_LINE);
        line = "}";
        stringBuilder.append(line);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE),
                HttpMethod.PUT, String.valueOf(stringBuilder), ResultStatus.class, params);

        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secrets 수정(Update Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateSecrets(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, params);
        Secrets secrets = commonService.setResultObject(responseMap, Secrets.class);
        secrets = commonService.annotationsProcessing(secrets, Secrets.class);

        resourceYamlService.updateSecrets(params, secrets);

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Vault Secrets 수정(Update Vault Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateVaultSecrets(Params params) {

        Map map = new HashMap();

        map.put("name", params.getMetadataName());
        map.put("defaultTtl", params.getDefaultTtl());
        map.put("maxTtl", params.getMaxTtl());

        ResultStatus resultStatus = restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                HttpMethod.PUT, templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map) , ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * 필터링된 Secrets 목록 조회(Get filtered Secrets list)
     *
     * @param responseMap the HashMap
     * @param params the params
     * @return the filtered Secrets list
     */
    SecretsList procSecretsList(HashMap responseMap, Params params){
        SecretsList secretsList = commonService.setResultObject(responseMap, SecretsList.class);
        secretsList.setItems(secretsList.getItems().stream()
                .filter(x -> !x.getMetadata().getName().contains(Constants.DEFAULT_SECRETS))
                .collect(Collectors.toList()));
        secretsList = commonService.resourceListProcessing(secretsList, params, SecretsList.class);
        return (SecretsList) commonService.setResultModel(secretsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * VaultDatabaseSecrets 설정(Set VaultDatabaseSecrets)
     *
     * @param params the params
     * @return the vault database secrets
     */
    private VaultDatabaseSecrets setVaultDatabaseSecrets(Params params){
        VaultDatabaseSecrets vaultDatabaseSecrets = new VaultDatabaseSecrets();
        if(!params.getResourceUid().equals(Constants.EMPTY_STRING))
            vaultDatabaseSecrets.setId(Long.parseLong(params.getResourceUid()));
        vaultDatabaseSecrets.setName(params.getMetadataName());
        if (params.getDbType().equals(VAULT_DATABASE_POSTGRES)) {
            vaultDatabaseSecrets.setDbType(POSTGRESQL_DATABASE);
        }
        vaultDatabaseSecrets.setFlag(CHECK_N);
        vaultDatabaseSecrets.setStatus(STATUS_UNKNOWN);

        return vaultDatabaseSecrets;
    }
}
