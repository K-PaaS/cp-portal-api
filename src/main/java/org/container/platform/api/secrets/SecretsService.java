package org.container.platform.api.secrets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.secrets.vaultSecrets.DatabaseConnections;
import org.container.platform.api.secrets.vaultSecrets.DatabaseConnectionsList;
import org.container.platform.api.secrets.vaultSecrets.DatabaseCredentials;
import org.container.platform.api.secrets.vaultSecrets.VaultDynamicSecretsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
    public DatabaseConnectionsList getVaultSecretsList(Params params) {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        DatabaseConnectionsList databaseConnectionsList = new DatabaseConnectionsList();

        ArrayList list = new ArrayList<Object>();
        Map<String, Object> map = new HashMap<String, Object>();

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretListUrl(), HttpMethod.GET, null, Map.class, params);
        VaultDynamicSecretsList vaultDynamicSecretsList = commonService.setResultObject(responseMap, VaultDynamicSecretsList.class);

        for (int i=0; i < vaultDynamicSecretsList.getItems().size(); i++) {
            String path = vaultDynamicSecretsList.getItems().get(i).getSpec().getPath();
            String createdTime = vaultDynamicSecretsList.getItems().get(i).getMetadata().getCreationTimestamp();
            map.put("createdTime", createdTime);
            //databaseConnections.setCreatedTime(createdTime);

            int idx = path.indexOf("/");
            String name = path.substring(idx+1);
            int idx2 = name.indexOf(SUB_STRING_ROLE);
            name = name.substring(0, idx2);
            map.put("name", name);
            //databaseConnections.setName(name);

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
                                //databaseConnections.setDbType(pluginName);
                                map.put("pluginName", pluginName);
                            }
                        }
                    }
                }
            }

            list.add(i, map);

            databaseConnectionsList.setItems(list);
        }

        return (DatabaseConnectionsList) commonService.setResultModel(databaseConnectionsList, Constants.RESULT_STATUS_SUCCESS);
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

        /*restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseCredentialsPath().replace("{name}", params.getMetadataName()),
                HttpMethod.GET, null, Object.class, params);*/

        return (Secrets) commonService.setResultModel(secrets, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Vault Secrets 상세 조회(Get Vault Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    public DatabaseCredentials getVaultSecrets(Params params) {
        DatabaseCredentials databaseCredentials = restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseCredentialsPath().replace("{name}", params.getResourceName()),
                HttpMethod.GET, null, DatabaseCredentials.class, params);

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
     * Vault Dynamic Secrets YAML 조회(Get Vault Dynamic Secrets Yaml)
     *
     * @param params the params
     * @return the Secrets yaml
     */
    public CommonResourcesYaml getVaultDynamicSecretsYaml(Params params){
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretListUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
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
            StringBuilder stringBuilder = new StringBuilder();

            map.put("name", params.getMetadataName());
            map.put("defaultTtl", params.getDefaultTtl());
            map.put("maxTtl", params.getMaxTtl());

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_config.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_policy.ftl", map) , ResultStatus.class, params);

            restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", params.getMetadataName()),
                    HttpMethod.POST, templateService.convert("create_vault_access_authentication_method_role.ftl", map) , ResultStatus.class, params);

            params.setYaml(templateService.convert("create_vault_dynamic_secret.ftl", map));

            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getVaultVaultDynamicSecretCreateUrl(),HttpMethod.POST, ResultStatus.class, params);
        }

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

        /*restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);*/

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Vault Secrets 삭제(Delete Vault Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteVaultSecrets(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

        /*restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseConnectionsPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultSecretsEnginesDatabaseRolesPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultPolicies().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);

        restTemplateService.sendVault(Constants.TARGET_VAULT_URL, propertyService.getVaultAccessAuthKubernetesRolesPath().replace("{name}", params.getMetadataName()),
                HttpMethod.DELETE, null , Object.class, params);*/

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
}
