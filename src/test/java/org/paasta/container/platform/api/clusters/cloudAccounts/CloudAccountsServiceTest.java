package org.paasta.container.platform.api.clusters.cloudAccounts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.clusters.support.AWSInfo;
import org.paasta.container.platform.api.clusters.clusters.support.GCPInfo;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.support.VaultResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CloudAccountsServiceTest {

    private static final String CLUSTER_API_URL = "https://localhost:6443";
    private static final String CLUSTER_NAME = "cp-cluster";
    private static final String CLUSTER_TOKEN = "";
    private static final String VAULT_PATH = "";

    private static CloudAccounts gResultModel = null;
    private static CloudAccounts gFinalResultModel = null;

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

        gResultModel = new CloudAccounts();
        gFinalResultModel = new CloudAccounts();

        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gGCPInfo = new GCPInfo();
        gAWSInfo = new AWSInfo();

    }

    @Test
    public void createCloudAccounts_AWS() {
        //given
        System.out.println("test");
        gParams.setProviderType(Constants.ProviderType.AWS);
        gParams.setProviderInfo(gAWSInfo);

        String path = "secret/test/1";
        Object providerInfo = gParams.getProviderInfo();

        when(propertyService.getCpVaultPathProviderCredential()).thenReturn("secret/{iaas}/{id}");
        when(vaultService.write(anyString(), any())).thenReturn(new VaultResponse());
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.POST, gResultModel, CloudAccounts.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        CloudAccounts result = cloudAccountsService.createCloudAccounts(gParams);

        System.out.println("result = " + result);
    }

    @Test
    public void getCloudAccounts() {
    }
}