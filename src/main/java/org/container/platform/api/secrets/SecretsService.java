package org.container.platform.api.secrets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.api.clusters.clusters.ClustersService;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.exception.ResultStatusException;
import org.container.platform.api.secrets.vaultSecrets.*;
import org.container.platform.api.workloads.deployments.DeploymentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

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
    private final VaultService vaultService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClustersService.class);

    /**
     * Instantiates a new Secrets service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     * @param resourceYamlService the resource yaml service
     * @param vaultService
     */
    @Autowired
    public SecretsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, ResourceYamlService resourceYamlService, TemplateService templateService, VaultService vaultService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resourceYamlService = resourceYamlService;
        this.templateService = templateService;
        this.vaultService = vaultService;
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

        List<Map<String,String>> searchList = null;
        searchList = new ArrayList<>();

        Map<String, String> map = null;
        Map<String, String> searchMap = null;

        VaultDatabaseSecretsList getVDSList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.GET, null, VaultDatabaseSecretsList.class, params);

        if (getVDSList.getItems().isEmpty()) {
            List<Map<String,String>> listEmpty = new ArrayList<>();
            databaseInfoList.setItems(listEmpty);
        }

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

        List resourceItemList;
        resourceItemList = databaseInfoList.getItems();

        if (params.getSearchName() != null && !params.getSearchName().equals("")) {
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i=0; i < resourceItemList.size(); i++) {
                HashMap hashMap = (HashMap) resourceItemList.get(i);
                Map<String, String> mapName = objectMapper.convertValue(hashMap, Map.class);
                for (Map.Entry<String, String> entry : mapName.entrySet()) {
                    if (entry.getKey().equals(RESOURCE_NAME)) {
                        String name = String.valueOf(entry.getValue());
                        if (name.contains(params.getSearchName().trim())) {
                            searchList.add(hashMap);
                        }
                    }
                }
            }

            CommonItemMetaData commonItemMetaData = commonService.setCommonItemMetaData(searchList, params.getOffset(), params.getLimit());
            databaseInfoList.setItemMetaData(commonItemMetaData);

            searchList = commonService.subListforLimit(searchList, params.getOffset(), params.getLimit());

            databaseInfoList.setItems(searchList);
            return (DatabaseInfoList) commonService.setResultModel(databaseInfoList, Constants.RESULT_STATUS_SUCCESS);
        }

        CommonItemMetaData commonItemMetaData = commonService.setCommonItemMetaData(resourceItemList, params.getOffset(), params.getLimit());
        databaseInfoList.setItemMetaData(commonItemMetaData);

        resourceItemList = commonService.subListforLimit(resourceItemList, params.getOffset(), params.getLimit());

        databaseInfoList.setItems(resourceItemList);
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

        HashMap tTl = vaultService.read(propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getResourceName()), HashMap.class);

        DatabaseRolesData databaseRolesData = new DatabaseRolesData();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map> mapTtl = objectMapper.convertValue(tTl, Map.class);
        if (databaseRolesData.getDefault_ttl() == null && databaseRolesData.getMax_ttl() == null) {
            for (Map.Entry<String, Map> entry : mapTtl.entrySet()) {
                Map data = entry.getValue();
                Map<String, Map> mapTtl2 = objectMapper.convertValue(data, Map.class);
                for (Map.Entry<String, Map> entry2 : mapTtl2.entrySet()) {
                    if (databaseRolesData.getDefault_ttl() == null && databaseRolesData.getMax_ttl() == null) {
                        Map data2 = entry2.getValue();
                        Map<String, String> mapTtl3 = objectMapper.convertValue(data2, Map.class);
                        for (Map.Entry<String, String> entry3 : mapTtl3.entrySet()) {
                            if (entry3.getKey().equals(DEFAULT_TTL)) {
                                String defaultTtl = String.valueOf(entry3.getValue());
                                int idx = defaultTtl.indexOf(".");
                                String defaultTtl2 = defaultTtl.substring(0, idx);
                                databaseRolesData.setDefault_ttl(defaultTtl2);
                            } else if (entry3.getKey().equals(MAX_TTL)) {
                                String maxTtl = String.valueOf(entry3.getValue());
                                int idx = maxTtl.indexOf(".");
                                String maxTtl2 = maxTtl.substring(0, idx);
                                databaseRolesData.setMax_ttl(maxTtl2);
                            }
                        }
                    }
                }
            }
        }

        DatabaseRoles databaseRoles = new DatabaseRoles();
        databaseRoles.setData(databaseRolesData);

        DatabaseCredentials databaseCredentials = new DatabaseCredentials();
        DatabaseCredentialsData databaseCredentialsData = new DatabaseCredentialsData();
        HashMap creds = vaultService.read(propertyService.getVaultSecretsEnginesDatabaseCredentialsPath().replace("{name}", params.getResourceName()), HashMap.class);

        ObjectMapper objectMapper2 = new ObjectMapper();
        Map<String, Map> mapCreds = objectMapper2.convertValue(creds, Map.class);
        for (Map.Entry<String, Map> entry : mapCreds.entrySet()) {
            Map value = entry.getValue();

            Map<String, Map> mapValue = objectMapper2.convertValue(value, Map.class);
            for (Map.Entry<String, Map> entry2 : mapValue.entrySet()) {
                if (entry2.getKey().equals(LEASE_DURATION)) {
                    String leaseDuration = String.valueOf(entry2.getValue());
                    int idx = leaseDuration.indexOf(".");
                    String leaseDuration2 = leaseDuration.substring(0, idx);
                    databaseCredentials.setLease_duration(leaseDuration2);
                } else if (entry2.getKey().equals(LEASE_ID)) {
                    String leaseId =  String.valueOf(entry2.getValue());
                    databaseCredentials.setLease_id(leaseId);
                } else if (entry2.getKey().equals(DATA)) {
                    Map account = entry2.getValue();
                    Map<String, Map> mapAccount = objectMapper2.convertValue(account, Map.class);
                    for (Map.Entry<String, Map> entry3 : mapAccount.entrySet()) {
                        if (entry3.getKey().equals(USERNAME)) {
                            String username = String.valueOf(entry3.getValue());
                            databaseCredentialsData.setUsername(username);
                        } else if (entry3.getKey().equals(PASSWORD)) {
                            String password = String.valueOf(entry3.getValue());
                            databaseCredentialsData.setPassword(password);
                        }
                    }
                }
            }
        }

        databaseCredentials.setData(databaseCredentialsData);

        HashMap dbType = vaultService.read(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getResourceName()), HashMap.class);

        ObjectMapper objectMapper3 = new ObjectMapper();
        Map<String, Map> mapData = objectMapper3.convertValue(dbType, Map.class);
        if (databaseCredentials.getPlugin_name() == null) {
            for (Map.Entry<String, Map> entry : mapData.entrySet()) {
                Map data = entry.getValue();

                Map<String, Map> mapData2 = objectMapper3.convertValue(data, Map.class);

                for (Map.Entry<String, Map> entry2 : mapData2.entrySet()) {
                    if (databaseCredentials.getPlugin_name() == null) {
                        Map pluginName = entry2.getValue();
                        Map<String, Map> mapData3 = objectMapper3.convertValue(pluginName, Map.class);
                        for (Map.Entry<String, Map> entry3 : mapData3.entrySet()) {
                            if (databaseCredentials.getPlugin_name() == null) {
                                if (entry3.getKey().equals(PLUGIN_NAME)) {
                                    String dBplugin = String.valueOf(entry3.getValue());
                                    if (dBplugin.equals(POSTGRESQL_DATABASE_PLUGIN)) {
                                        databaseCredentials.setPlugin_name(POSTGRESQL_DATABASE);
                                    };
                                }
                            }
                        }
                    }
                }
            }
        }

        databaseCredentials.setDefault_ttl(databaseRoles.getData().getDefault_ttl());
        databaseCredentials.setMax_ttl(databaseRoles.getData().getMax_ttl());

        //DB 상세 조회
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
            try {
                restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.POST, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);
            } catch (DuplicateKeyException e) {
                LOGGER.info("DB_REGISTRATION_FAILED : " + CommonUtils.loggerReplace(e.getMessage()));
                throw new ResultStatusException(MessageConstant.DUPLICATE_NAME.getMsg());
            }

            //database-secret config 생성
            vaultService.write(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()), templateService.convert("create_vault_secret_engine_database_postgres_config.ftl", map));

            //database-secret role 생성
            vaultService.write(propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()), templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map));

            //database-secret/creds의 policy 생성
            vaultService.write(propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()), templateService.convert("create_vault_policy.ftl", map));

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
        vaultService.write(propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE + "-" + params.getDbService()), templateService.convert("create_vault_access_authentication_method_role.ftl", map));

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

        int idx = resourceYaml.indexOf("        name:");
        yamlHead = resourceYaml.substring(0, idx);
        yamlBody = resourceYaml.substring(idx);

        //yaml 수정
        stringBuilder.append(yamlHead);
        stringBuilder.append(templateService.convert("create_vault_secret_inject.ftl", map));
        stringBuilder.append(yamlBody);
        params.setYaml(String.valueOf(stringBuilder));

        //DB 수정 Logic Application 이름, namespace 인풋
        VaultDatabaseSecrets vaultDatabaseSecrets = new VaultDatabaseSecrets();
        vaultDatabaseSecrets.setName(params.getDbService());
        vaultDatabaseSecrets.setAppName(params.getResourceName());
        vaultDatabaseSecrets.setAppNamespace(params.getNamespace());
        vaultDatabaseSecrets.setFlag(CHECK_Y);

        restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.PUT, vaultDatabaseSecrets, VaultDatabaseSecrets.class, params);

        //yaml 생성
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
            int idx2 = resourceYaml.indexOf("        image:");
            int idx3 = resourceYaml.indexOf("        imagePullPolicy:");

            yamlHead = resourceYaml.substring(0, idx);
            yamlImage = resourceYaml.substring(idx2, idx3);
            yamlBody = resourceYaml.substring(idx3);

            String yamlImage2 = yamlImage.substring(8);

            stringBuilder.append(yamlHead);
            stringBuilder.append("      - " + yamlImage2);
            stringBuilder.append(yamlBody);

            params.setYaml(String.valueOf(stringBuilder));

            //Deployment 적용 해제
            restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);

            //ServiceAccount 삭제
            restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListUsersDeleteUrl().replace("{namespace}", params.getNamespace()).replace("{name}", params.getDbService() + "-dynamic-service-account"),
                    HttpMethod.DELETE, null, ResultStatus.class, params);

            //k8s-auth-role 삭제
            vaultService.delete(propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", Constants.K8S_AUTH_ROLE + "-" + params.getDbService()));

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

        //database-secret config 삭제
        vaultService.delete(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getDbService()));

        //database-secret role 생성
        vaultService.delete(propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getDbService()));

        //database-secret/creds의 policy 삭제
        vaultService.delete(propertyService.getVaultPolicies().replace("{name}", params.getDbService()));

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
        ResultStatus resultStatus = new ResultStatus();
        Map map = new HashMap();

        map.put("name", params.getMetadataName());
        map.put("defaultTtl", params.getDefaultTtl());
        map.put("maxTtl", params.getMaxTtl());

        vaultService.write(propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()), templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map));
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
