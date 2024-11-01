package org.container.platform.api.secrets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.secrets.vaultSecrets.*;
import org.container.platform.api.workloads.deployments.Deployments;
import org.container.platform.api.workloads.deployments.DeploymentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
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

        VaultDatabaseSecretsList getVDSList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.GET, null, VaultDatabaseSecretsList.class, params);
        System.out.println(getVDSList);

        for (int i=0; i < getVDSList.getItems().size(); i++) {

            map = new HashMap<>();
            map.put("name", getVDSList.getItems().get(i).getName());
            map.put("pluginName", getVDSList.getItems().get(i).getDbType());
            map.put("createdTime", getVDSList.getItems().get(i).getCreated());

            if (getVDSList.getItems().get(i).getAppName() != null && getVDSList.getItems().get(i).getFlag().equals(CHECK_Y)) {

                params.setResourceName(getVDSList.getItems().get(i).getAppName());
                params.setNamespace(getVDSList.getItems().get(i).getAppNamespace());

                HashMap responseMap2 = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListDeploymentsListUrl(), HttpMethod.GET, null, Map.class, params);
                DeploymentsList deploymentsList = commonService.setResultObject(responseMap2, DeploymentsList.class);

                for (int j=0; j < deploymentsList.getItems().size(); j++) {
                    Object obj = deploymentsList.getItems().get(j);
                    String objStr = obj.toString();

                    int idx3 = objStr.indexOf("name=");
                    int idx4 = objStr.indexOf(", namespace=");

                    String appName = objStr.substring(idx3+5,idx4);

                    if (params.getResourceName().equals(appName)) {
                        int idx5 = objStr.indexOf("runningPods=");
                        int idx6 = objStr.indexOf("totalPods=");
                        int idx7 = objStr.indexOf("images=");

                        String runningPods = objStr.substring(idx5+12,idx6-2);
                        String totalPods = objStr.substring(idx6+10,idx7-2);

                        if (totalPods.equals(runningPods)) {
                            map.put("applicableStatus", STATUS_ON);
                        } else {
                            map.put("applicableStatus", STATUS_HOLD);
                        }
                    }

                }

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
        databaseCredentials.setApplication(getVDS.getAppName());
        params.setResourceName(getVDS.getAppName());
        params.setNamespace(getVDS.getAppNamespace());

        if (getVDS.getAppName() != null && getVDS.getFlag().equals(CHECK_Y)) {

            HashMap responseMap2 = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsListUrl(), HttpMethod.GET, null, Map.class, params);
            DeploymentsList deploymentsList = commonService.setResultObject(responseMap2, DeploymentsList.class);

            for (int j=0; j < deploymentsList.getItems().size(); j++) {
                Object obj = deploymentsList.getItems().get(j);
                String objStr = obj.toString();

                int idx3 = objStr.indexOf("name=");
                int idx4 = objStr.indexOf(", namespace=");

                String appName = objStr.substring(idx3+5,idx4);

                if (params.getResourceName().equals(appName)) {
                    int idx5 = objStr.indexOf("runningPods=");
                    int idx6 = objStr.indexOf("totalPods=");
                    int idx7 = objStr.indexOf("images=");

                    String runningPods = objStr.substring(idx5+12,idx6-2);
                    String totalPods = objStr.substring(idx6+10,idx7-2);
                    String namespace = objStr.substring(idx4+12,idx5-2);

                    databaseCredentials.setNamespace(namespace);

                    if (totalPods.equals(runningPods)) {
                        databaseCredentials.setStatus(STATUS_ON);
                    } else {
                        databaseCredentials.setStatus(STATUS_HOLD);
                    }
                }

            }

        } else {
            databaseCredentials.setStatus(STATUS_OFF);
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

        if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_KUBERNETES)) {
            resourceYamlService.createSecrets(params);

            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListSecretsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        } else if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_VAULT)) {

            Map map = new HashMap();

            map.put("name", params.getMetadataName());
            map.put("defaultTtl", params.getDefaultTtl());
            map.put("maxTtl", params.getMaxTtl());
            map.put("serviceName", params.getDbService());

            //DB에 database-secret 이름 저장
            VaultDatabaseSecrets vaultDatabaseSecrets = setVaultDatabaseSecrets(params);
            restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.POST, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

            // database-secret config 생성
            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_config.ftl", map) , ResultStatus.class, params);

            // database-secret role 생성
            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map) , ResultStatus.class, params);

            // database-secret/creds의 policy 생성
            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_policy.ftl", map) , ResultStatus.class, params);
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

        map.put("db_name", params.getDbService());
        map.put("app_name", params.getResourceName());
        map.put("namespace", params.getNamespace());

        String yamlHead = "";
        String yamlBody = "";

        //ServiceAccount 생성
        params.setYaml(templateService.convert("create_vault_service_account.ftl", map));

        restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        //k8s-auth-role 생성
        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE + "-" + params.getDbService()),
                HttpMethod.POST, templateService.convert("create_vault_access_authentication_method_role.ftl", map), ResultStatus.class, params);

        //VaultAuth 생성
        params.setYaml(templateService.convert("create_vault_auth.ftl", map));

        restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultAuthCreateUrl().replace("{namespace}", params.getNamespace()), HttpMethod.POST, ResultStatus.class, params);

        //VaultDynamicSecret 생성
        params.setYaml(templateService.convert("create_vault_dynamic_secret.ftl", map));

        restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretCreateUrl(),HttpMethod.POST, ResultStatus.class, params);

        // 적용 deployment yaml 조회
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);

        int idx = resourceYaml.indexOf("        ports:");
        yamlHead = resourceYaml.substring(0, idx);
        yamlBody = resourceYaml.substring(idx);

        // yaml 수정
        stringBuilder.append(yamlHead);
        stringBuilder.append(templateService.convert("create_vault_secret_inject.ftl", map));
        String secret = templateService.convert("create_vault_secret_inject.ftl", map);
        System.out.println(secret);
        stringBuilder.append(yamlBody);
        params.setYaml(String.valueOf(stringBuilder));

        //DB 수정 Logic Application 이름, namespace 인풋
        VaultDatabaseSecrets vaultDatabaseSecrets = new VaultDatabaseSecrets();
        vaultDatabaseSecrets.setName(params.getDbService());
        vaultDatabaseSecrets.setAppName(params.getResourceName());
        vaultDatabaseSecrets.setAppNamespace(params.getNamespace());
        vaultDatabaseSecrets.setFlag(CHECK_Y);

        restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.PUT, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

        // yaml 생성
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
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
        ResultStatus resultStatus = new ResultStatus();

        String line = "";
        String yamlHead = "";
        String yamlImage = "";
        String yamlBody = "";

        VaultDatabaseSecrets getVDS = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", params.getResourceName()), HttpMethod.GET, null, VaultDatabaseSecrets.class, params);

        params.setDbService(getVDS.getName());

        if (getVDS.getAppName() != null && getVDS.getAppNamespace() != null) {
            params.setResourceName(getVDS.getAppName());
            params.setNamespace(getVDS.getAppNamespace());

            String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);

            int idx = resourceYaml.indexOf("      - envFrom:");
            int idx2 = resourceYaml.indexOf("            name:");
            int idx3 = resourceYaml.indexOf("        image:");
            int idx4 = resourceYaml.indexOf("        imagePullPolicy:");

            yamlHead = resourceYaml.substring(0, idx);
            yamlImage = resourceYaml.substring(idx3, idx4);
            yamlBody = resourceYaml.substring(idx4);

            String yamlImage2 = yamlImage.substring(8);

            stringBuilder.append(yamlHead);
            stringBuilder.append("      - " + yamlImage2);
            stringBuilder.append(yamlBody);

            params.setYaml(String.valueOf(stringBuilder));

            //Deployment 적용 해제
            restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);

            //serviceaccount 삭제
            restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getDbService() + "-dynamic-service-account"),
                    HttpMethod.DELETE, null, ResultStatus.class, params);

            //VaultAuth 삭제
            restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getVaultVaultAuthDeleteUrl().replace("{name}", params.getDbService()).replace("{namespace}", params.getNamespace()),
                    HttpMethod.DELETE, null, ResultStatus.class, params);

            //VaultDynamicSecret 삭제
            restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getVaultVaultDynamicSecretDeleteUrl().replace("{name}", params.getDbService()).replace("{namespace}", params.getNamespace()) , HttpMethod.DELETE, null, ResultStatus.class, params);

        }

        //DB 삭제
       restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", params.getDbService()), HttpMethod.DELETE, null, VaultDatabaseSecrets.class, params);

        // database-secret config 삭제
        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getDbService()),
                HttpMethod.DELETE, null , Object.class, params);

        // database-secret role 생성
        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getDbService()),
                HttpMethod.DELETE, null , Object.class, params);

        // database-secret/creds의 policy 삭제
        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getDbService()),
                HttpMethod.DELETE, null , Object.class, params);

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
