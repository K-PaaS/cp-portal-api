package org.paasta.container.platform.api.clusters.clusters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.VaultService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.support.VaultResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClustersServiceTest {
    private static final String CLUSTER_API_URL = "https://localhost:6443";
    private static final String CLUSTER_NAME = "cp-cluster";
    private static final String CLUSTER_TOKEN = "";
    private static final String VAULT_PATH = "";

    private static Clusters gResultModel = null;
    private static Clusters gFinalResultModel = null;

    private static Params gParams = null;
    private static VaultResponse gVaultResultModel = null;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private VaultService vaultService;

    @InjectMocks
    private ClustersService clustersService;

    @Before
    public void setUp() {
        gResultModel = new Clusters();
        gResultModel.setClusterName(CLUSTER_NAME);

        gFinalResultModel = new Clusters();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gParams = new Params();

        gParams.setCluster(CLUSTER_NAME);

        gVaultResultModel = new VaultResponse();
    }

    /**
     * Clusters 정보 저장(Create Clusters Info) Test
     */
    @Test
    public void createClusters_Valid_ReturnModel() {
        // given
        Clusters clusters = new Clusters();
        clusters.setClusterName(gParams.getResourceName());

        when(vaultService.write("", gParams.getProviderInfo())).thenReturn(gVaultResultModel);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.createClusters(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Clusters 정보 조회(Get Clusters Info) Test
     */
    @Test
    public void getClusters_Valid_ReturnModel() {
        // given
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters/" + gParams.getCluster(), HttpMethod.GET, null, Clusters.class, gParams)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.getClusters(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }
}
