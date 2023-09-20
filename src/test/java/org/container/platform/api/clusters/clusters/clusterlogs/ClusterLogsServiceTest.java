package org.container.platform.api.clusters.clusters.clusterlogs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClusterLogsServiceTest {

    private static ClusterLogsList gFinalClusterLogsList;
    private static Params gParams;

    @Mock
    CommonService commonService;

    @Mock
    RestTemplateService restTemplateService;

    @InjectMocks
    ClusterLogsService clusterLogsService;

    @Before
    public void setUp() throws Exception {
        gFinalClusterLogsList = new ClusterLogsList();
        gFinalClusterLogsList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gParams = new Params();
        gParams.setCluster("test");
    }

    @Test
    public void getClusterLogs() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/logs/" + gParams.getCluster(), HttpMethod.GET, null, ClusterLogsList.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalClusterLogsList);
        ClusterLogsList result = clusterLogsService.getClusterLogs(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);

    }
}