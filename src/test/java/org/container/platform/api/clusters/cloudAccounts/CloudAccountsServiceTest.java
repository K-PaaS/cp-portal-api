package org.container.platform.api.clusters.cloudAccounts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.clusters.clusters.support.AWSInfo;
import org.container.platform.api.clusters.clusters.support.GCPInfo;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.support.VaultResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CloudAccountsServiceTest {

    private static final String CLUSTER_API_URL = "https://localhost:6443";
    private static final String CLUSTER_NAME = "cp-cluster";
    private static final String CLUSTER_TOKEN = "";
    private static final String VAULT_PATH = "";
    private static final String RESOURCE_ID = "1";
    private static final String RESOURCE_NAME = "accounts";
    private static final String PROVIDER_TYPE = Constants.ProviderType.AWS.name();
    private static final String REGION = "Region";


    private static CloudAccounts gResultModel = null;
    private static CloudAccounts gFinalResultModel = null;

    private static CloudAccountsList gResultListModel = null;
    private static CloudAccountsList gFinalResultListModel = null;

    private static Params gParams = null;
    private static VaultResponse gVaultResultModel = null;

    private static GCPInfo gGCPInfo = null;
    private static AWSInfo gAWSInfo = null;

    @Mock
    private RestTemplateService restTemplateService;
    @Mock
    private VaultService vaultService;
    @Mock
    private CommonService commonService;
    @Mock
    private PropertyService propertyService;
    @InjectMocks
    private CloudAccountsService cloudAccountsService;


    @Before
    public void setUp() {

        gParams = new Params();
        gAWSInfo = new AWSInfo();
        gGCPInfo = new GCPInfo();

        gParams.setResourceUid(RESOURCE_ID);
        gParams.setResourceName(RESOURCE_NAME);
        gParams.setProviderType(Constants.ProviderType.AWS);
        gParams.setProviderInfo(gAWSInfo);

        gResultModel = new CloudAccounts();
        gResultModel.setId(Long.parseLong(RESOURCE_ID));
        gResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultModel.setProvider(Constants.ProviderType.AWS.name());

        gFinalResultModel = new CloudAccounts();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        List<CloudAccounts> listItemModel = new ArrayList<>();
        listItemModel.add(gResultModel);

        gResultListModel = new CloudAccountsList();
        gResultListModel.setItems(listItemModel);

        gFinalResultListModel = new CloudAccountsList();
        gFinalResultListModel.setItems(listItemModel);
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);



    }

    @Test
    public void createCloudAccounts_Valid() {
        //given
        CloudAccounts cloudAccounts = new CloudAccounts();
        cloudAccounts.setName(RESOURCE_NAME);
        cloudAccounts.setProvider(PROVIDER_TYPE);
        cloudAccounts.setRegion(REGION);
        cloudAccounts.setId(Long.parseLong(RESOURCE_ID));

        when(propertyService.getCpVaultPathProviderCredential()).thenReturn("secret/AWS/1");
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.POST, cloudAccounts, CloudAccounts.class, gParams)).thenReturn(gResultModel);
        when(commonService.setResultObject(gParams.getProviderInfo(), AWSInfo.class)).thenReturn(gAWSInfo);
        when(vaultService.write("secret/AWS/1", gAWSInfo)).thenReturn(new VaultResponse());
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        try {
            // when
            CloudAccounts result = cloudAccountsService.createCloudAccounts(gParams);
        } catch (Exception e) {}


//        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getCloudAccounts_Valid() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id:.+}"
                .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.GET, null, CloudAccounts.class, gParams)).thenReturn(gResultModel);
        when(propertyService.getCpVaultPathProviderCredential()).thenReturn("test");
        when(vaultService.read("test", AWSInfo.class)).thenReturn(gAWSInfo);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        CloudAccounts result = cloudAccountsService.getCloudAccounts(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getCloudAccountsList_Valid(){
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.GET, null, CloudAccountsList.class, gParams)).thenReturn(gResultListModel);
        when(commonService.globalListProcessing(gResultListModel, gParams, CloudAccountsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        CloudAccountsList result = cloudAccountsService.getCloudAccountsList(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }


    @Test
    public void updateCloudAccounts(){
        //check id
        CloudAccounts cloudAccounts = new CloudAccounts();
        cloudAccounts.setName(RESOURCE_NAME);
        cloudAccounts.setProvider(PROVIDER_TYPE);
        cloudAccounts.setRegion(REGION);
        cloudAccounts.setId(Long.parseLong(RESOURCE_ID));

        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.PATCH, cloudAccounts, CloudAccounts.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
        CloudAccounts result = cloudAccountsService.updateCloudAccounts(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void deleteCloudAccounts_Valid(){
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id:.+}"
                .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.DELETE, null, CloudAccounts.class, gParams)).thenReturn(gResultModel);

        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        CloudAccounts result = cloudAccountsService.deleteCloudAccounts(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }




}