package org.container.platform.api.secrets;


import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
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
    public void getSecrets() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Secrets.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultModel);

        secretsService.getSecrets(gParams);
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
    public void procSecretsList() {
        when(commonService.setResultObject(gResultMap, SecretsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, SecretsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultListModel);
    }
}