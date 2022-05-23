package org.paasta.container.platform.api.clusters.clusters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClustersServiceTest {
    private static final String CLUSTER_API_URL = "https://localhost:6443";
    private static final String CLUSTER_NAME = "cp-cluster";
    private static final String CLUSTER_TOKEN = "";

    private static Clusters gResultModel = null;
    private static Clusters gFinalResultModel = null;

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private ClustersService clustersService;

    @Before
    public void setUp() {
        gResultModel = new Clusters();
        gResultModel.setClusterApiUrl(CLUSTER_API_URL);
        gResultModel.setClusterName(CLUSTER_NAME);
        gResultModel.setClusterToken(CLUSTER_TOKEN);

        gFinalResultModel = new Clusters();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Clusters 정보 저장(Create Clusters Info) Test
     */
    @Test
    public void createClusters_Valid_ReturnModel() {
        // given
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, gResultModel, Clusters.class)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.createClusters(CLUSTER_API_URL, CLUSTER_NAME, CLUSTER_TOKEN);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Clusters 정보 조회(Get Clusters Info) Test
     */
    @Test
    public void getClusters_Valid_ReturnModel() {
        // given
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters/" + CLUSTER_NAME, HttpMethod.GET, null, Clusters.class)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.getClusters(CLUSTER_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }
}
