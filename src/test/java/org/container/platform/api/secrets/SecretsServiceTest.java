package org.container.platform.api.secrets;


import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
import org.container.platform.api.secrets.vaultSecrets.*;
import org.container.platform.api.workloads.deployments.DeploymentsList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class SecretsServiceTest {

    @Mock
    RestTemplateService restTemplateService;
    @Mock
    CommonService commonService;
    @Mock
    PropertyService propertyService;
    @Mock
    ResourceYamlService resourceYamlService;
    @Mock
    TemplateService templateService;
    @Mock
    VaultService vaultService;

    @InjectMocks
    SecretsService secretsService;

    private static final String YAML_STRING = "test-string";

    private static Params gParams = null;
    private static HashMap gResultMap = null;
    private static Map gMap = null;
    private static SecretsList gResultListModel = null;
    private static Secrets gResultModel = null;
    private static CommonResourcesYaml gResultYaml = null;
    private static ResultStatus gResultStatus = null;
    private static AuthKubernetesRoles gAuthKubernetesRolesModel = null;
    private static AuthKubernetesRolesData gAuthKubernetesRolesDataModel = null;
    private static DatabaseConnections gDatabaseConnectionsModel = null;
    private static DatabaseConnectionsData gDatabaseConnectionsDataModel = null;
    private static DatabaseCredentials gDatabaseCredentialsModel = null;
    private static DatabaseCredentialsData gDatabaseCredentialsDataModel = null;
    private static DatabaseInfo gDatabaseInfoModel = null;
    private static DatabaseInfoList gDatabaseInfoListModel = null;
    private static DatabaseRoles gDatabaseRolesModel = null;
    private static DatabaseRolesData gDatabaseRolesDataModel = null;
    private static VaultDatabaseSecrets gVaultDatabaseSecretsModel = null;
    private static VaultDatabaseSecretsList gVaultDatabaseSecretsListModel = null;
    private static VaultDynamicSecrets gVaultDynamicSecretsModel = null;
    private static VaultDynamicSecretsList gVaultDynamicSecretsListModel = null;
    private static VaultDynamicSecretsMetadata gVaultDynamicSecretsMetadataModel = null;
    private static VaultDynamicSecretsSpec gVaultDynamicSecretsSpecModel = null;
    private static DeploymentsList gDeploymentsListModel = null;
    private static CommonItemMetaData gCommonItemMetaDataModel = null;

    @Before
    public void setUp() {
        gParams = new Params();
        gParams.setMetadataName("name");
        gResultMap = new HashMap();
        gMap = new HashMap();
        gResultListModel = new SecretsList();
        gResultModel = new Secrets();
        gResultYaml = new CommonResourcesYaml("");
        gResultStatus = new ResultStatus();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        gResultModel.setMetadata(commonMetaData);

        List<Secrets> itemList = new ArrayList<>();
        itemList.add(gResultModel);
        gResultListModel.setItems(itemList);

        gAuthKubernetesRolesModel = new AuthKubernetesRoles();
        gAuthKubernetesRolesModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gAuthKubernetesRolesModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gAuthKubernetesRolesModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gAuthKubernetesRolesModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gAuthKubernetesRolesModel.setData(gAuthKubernetesRolesDataModel);

        gAuthKubernetesRolesDataModel = new AuthKubernetesRolesData();
        ArrayList<AuthKubernetesRolesData> authKubernetesRolesData = new ArrayList<>();
        gAuthKubernetesRolesDataModel.setToken_policies(authKubernetesRolesData);

        gDatabaseConnectionsModel = new DatabaseConnections();
        gDatabaseConnectionsModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDatabaseConnectionsModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDatabaseConnectionsModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDatabaseConnectionsModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gDatabaseConnectionsModel.setData(gDatabaseConnectionsDataModel);

        gDatabaseConnectionsDataModel = new DatabaseConnectionsData();
        gDatabaseConnectionsDataModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDatabaseConnectionsDataModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDatabaseConnectionsDataModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDatabaseConnectionsDataModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        List<DatabaseConnectionsData> databaseConnectionsData = new ArrayList<>();
        gDatabaseConnectionsDataModel.setData(databaseConnectionsData);

        gDatabaseCredentialsModel = new DatabaseCredentials();
        gDatabaseCredentialsModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDatabaseCredentialsModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDatabaseCredentialsModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDatabaseCredentialsModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gDatabaseCredentialsModel.setLease_id("leaseId");
        gDatabaseCredentialsModel.setLease_duration("5m");
        gDatabaseCredentialsModel.setDefault_ttl("5m");
        gDatabaseCredentialsModel.setMax_ttl("5m");
        gDatabaseCredentialsModel.setPlugin_name("postgresql-database-plugin");
        gDatabaseCredentialsModel.setApplication("testApp");
        gDatabaseCredentialsModel.setNamespace("default");
        gDatabaseCredentialsModel.setStatus("On");
        gDatabaseCredentialsModel.setFlag("Y");
        gDatabaseCredentialsModel.setData(gDatabaseCredentialsDataModel);

        gDatabaseCredentialsDataModel = new DatabaseCredentialsData();
        gDatabaseCredentialsDataModel.setPassword("password");
        gDatabaseCredentialsDataModel.setUsername("username");

        gDatabaseInfoModel = new DatabaseInfo();
        gDatabaseInfoModel.setName("test");
        gDatabaseInfoModel.setDbType("postgresql");
        gDatabaseInfoModel.setCreatedTime("2024-11-25T09:31:37Z");
        gDatabaseInfoModel.setStatus("On");

        gDatabaseInfoListModel = new DatabaseInfoList();
        gDatabaseInfoListModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDatabaseInfoListModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDatabaseInfoListModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDatabaseInfoListModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gDatabaseInfoListModel.setItemMetaData(new CommonItemMetaData());
        gDatabaseInfoListModel.setItems(new ArrayList<>());

        gDatabaseRolesModel = new DatabaseRoles();
        gDatabaseRolesModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDatabaseRolesModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDatabaseRolesModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDatabaseRolesModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gDatabaseRolesModel.setData(gDatabaseRolesDataModel);

        gDatabaseRolesDataModel = new DatabaseRolesData();
        gDatabaseRolesDataModel.setDefault_ttl("5m");
        gDatabaseRolesDataModel.setMax_ttl("5m");

        gVaultDatabaseSecretsModel = new VaultDatabaseSecrets();
        gVaultDatabaseSecretsModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gVaultDatabaseSecretsModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gVaultDatabaseSecretsModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gVaultDatabaseSecretsModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gVaultDatabaseSecretsModel.setId(1);
        gVaultDatabaseSecretsModel.setName("test");
        gVaultDatabaseSecretsModel.setAppName("cp-app");
        gVaultDatabaseSecretsModel.setAppNamespace("default");
        gVaultDatabaseSecretsModel.setStatus("On");
        gVaultDatabaseSecretsModel.setFlag("Y");
        gVaultDatabaseSecretsModel.setCreated("2024-11-25T09:31:37Z");

        gVaultDatabaseSecretsListModel = new VaultDatabaseSecretsList();
        gVaultDatabaseSecretsListModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gVaultDatabaseSecretsListModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gVaultDatabaseSecretsListModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gVaultDatabaseSecretsListModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        List<VaultDatabaseSecrets> vaultDatabaseSecrets = new ArrayList<>();
        gVaultDatabaseSecretsListModel.setItems(vaultDatabaseSecrets);

        gVaultDynamicSecretsModel = new VaultDynamicSecrets();
        gVaultDynamicSecretsModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gVaultDynamicSecretsModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gVaultDynamicSecretsModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gVaultDynamicSecretsModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gVaultDynamicSecretsModel.setSpec(gVaultDynamicSecretsSpecModel);
        gVaultDynamicSecretsModel.setMetadata(gVaultDynamicSecretsMetadataModel);

        gVaultDynamicSecretsListModel = new VaultDynamicSecretsList();
        gVaultDynamicSecretsListModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gVaultDynamicSecretsListModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gVaultDynamicSecretsListModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gVaultDynamicSecretsListModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        List<VaultDynamicSecrets> vaultDynamicSecrets = new ArrayList<>();
        gVaultDynamicSecretsListModel.setItems(vaultDynamicSecrets);

        gVaultDynamicSecretsMetadataModel = new VaultDynamicSecretsMetadata();
        gVaultDynamicSecretsMetadataModel.setCreationTimestamp("2024-11-25T09:31:37Z");

        gVaultDynamicSecretsSpecModel = new VaultDynamicSecretsSpec();
        gVaultDynamicSecretsSpecModel.setPath("database-secret/");

        gDeploymentsListModel = new DeploymentsList();
        gCommonItemMetaDataModel = new CommonItemMetaData();
        gDeploymentsListModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gDeploymentsListModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gDeploymentsListModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gDeploymentsListModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
        gDeploymentsListModel.setItemMetaData(gCommonItemMetaDataModel);
    }

    @Test
    public void getSecretsList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        procSecretsList();

        SecretsList result = secretsService.getSecretsList(gParams);
        assertNotEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getVaultSecretsList() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.GET, null, VaultDatabaseSecretsList.class, gParams)).thenReturn(gVaultDatabaseSecretsListModel);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, DeploymentsList.class)).thenReturn(gDeploymentsListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultModel);
    }

    @Test
    public void getSecrets() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Secrets.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultModel);

        secretsService.getSecrets(gParams);
    }

    @Test
    public void getVaultSecrets() {
        when(vaultService.read(propertyService.getVaultSecretsEnginesDatabaseRolesPath(), HashMap.class)).thenReturn(gResultMap);
        when(vaultService.read(propertyService.getVaultSecretsEnginesDatabaseCredentialsPath(), HashMap.class)).thenReturn(gResultMap);
        when(vaultService.read(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath(), HashMap.class)).thenReturn(gResultMap);
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", gParams.getResourceName()), HttpMethod.GET, null, VaultDatabaseSecrets.class, gParams)).thenReturn(gVaultDatabaseSecretsModel);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, DeploymentsList.class)).thenReturn(gDeploymentsListModel);

        when(commonService.setResultModel(gDatabaseCredentialsModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
    }

    @Test
    public void getSecretsYaml() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultYaml);

        secretsService.getSecretsYaml(gParams);
    }

    @Test
    public void createSecrets() {
        when(resourceYamlService.createSecrets(gParams)).thenReturn(gResultStatus);
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.POST, gVaultDatabaseSecretsListModel, VaultDatabaseSecrets.class, gParams);
        when(templateService.convert("create_vault_secret_engine_database_postgres_config.ftl", gResultMap)).thenReturn("database_postgres_config");
        when(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath()).thenReturn("database-secret/config/name-db");
        when(vaultService.write("database-secret/config/name-db", "database_postgres_config")).thenReturn("");
        when(templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", gResultMap)).thenReturn("database_postgres_role");
        when(propertyService.getVaultSecretsEnginesDatabaseRolesPath()).thenReturn("database-secret/roles/name-role");
        when(vaultService.write("database-secret/roles/name-role", "database_postgres_role")).thenReturn("");
        when(templateService.convert("create_vault_policy.ftl", gResultMap)).thenReturn("create_vault_policy");
        when(propertyService.getVaultPolicies()).thenReturn("sys/policies/acl/name-policy");
        when(vaultService.write("sys/policies/acl/name-policy", "create_vault_policy")).thenReturn("");
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
    }

    @Test
    public void applyVaultSecrets() {
        when(templateService.convert("create_vault_service_account.ftl", gResultMap)).thenReturn("service_account");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(templateService.convert("create_vault_access_authentication_method_role.ftl", gResultMap)).thenReturn("access_authentication_method_role");
        when(propertyService.getVaultAccessAuthKubernetesRolesPath()).thenReturn("auth/k8s-auth-mount/role/name-role");
        when(vaultService.write("auth/k8s-auth-mount/role/name-role", "access_authentication_method_role")).thenReturn("");
        when(templateService.convert("create_vault_auth.ftl", gResultMap)).thenReturn("vault_auth");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultAuthCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(templateService.convert("create_vault_dynamic_secret.ftl", gResultMap)).thenReturn("vault_dynamic_secret");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretCreateUrl(),HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn("resourceYaml");
        when(templateService.convert("create_vault_secret_inject.ftl", gResultMap)).thenReturn("vault_secret_inject");
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets", HttpMethod.PUT, gVaultDatabaseSecretsListModel, VaultDatabaseSecrets.class, gParams)).thenReturn(null);
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
    }

    @Test
    public void deleteSecrets() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);

        secretsService.deleteSecrets(gParams);
    }

    @Test
    public void deleteVaultSecrets() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", gParams.getResourceName()), HttpMethod.GET, null, VaultDatabaseSecrets.class, gParams)).thenReturn(gVaultDatabaseSecretsModel);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(propertyService.getVaultAccessAuthKubernetesRolesPath()).thenReturn("auth/k8s-auth-mount/role/name-role");
        vaultService.delete("auth/k8s-auth-mount/role/name-role");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultAuthDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getVaultVaultDynamicSecretDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/vaultDatabaseSecrets/{name:.+}"
                .replace("{name:.+}", gParams.getDbService()), HttpMethod.DELETE, null, VaultDatabaseSecrets.class, gParams)).thenReturn(null);
        when(propertyService.getVaultSecretsEnginesDatabaseConnectionsPath()).thenReturn("database-secret/config/name-db");
        vaultService.delete("database-secret/config/name-db");
        when(propertyService.getVaultSecretsEnginesDatabaseRolesPath()).thenReturn(null);
        vaultService.delete("database-secret/roles/name-role");
        when(propertyService.getVaultPolicies()).thenReturn("sys/policies/acl/name-policy");
        vaultService.delete("sys/policies/acl/name-policy");
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
    }

    @Test
    public void updateSecrets() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gMap);
        when(commonService.setResultObject(gMap, Secrets.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, Secrets.class)).thenReturn(gResultModel);
        resourceYamlService.updateSecrets(gParams, gResultModel);
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
    }

    @Test
    public void updateVaultSecrets() {
        when(templateService.convert("create_vault_secret_engine_database_postgres_role.ftl", gResultMap)).thenReturn("database_postgres_role");
        when(propertyService.getVaultSecretsEnginesDatabaseRolesPath()).thenReturn("database-secret/roles/name-role");
        when(vaultService.write("database-secret/roles/name-role", "database_postgres_role")).thenReturn("");
        when(commonService.setResultObject(gResultMap, SecretsList.class)).thenReturn(gResultListModel);
    }
    @Test
    public void procSecretsList() {
        when(commonService.setResultObject(gResultMap, SecretsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, SecretsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultListModel);
    }
}