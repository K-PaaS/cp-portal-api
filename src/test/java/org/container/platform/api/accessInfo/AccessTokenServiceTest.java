package org.container.platform.api.accessInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.clusters.clusters.Clusters;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonStatusCode;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.roles.RolesList;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class AccessTokenServiceTest {
    private static final String NAMESPACE = "test-namespace";
    private static final String NAME = "test-access-token-name";

    private static final String CA_CERT_TOKEN = "dGVzdC1jYS1jZXJ0LXRva2Vu";
    private static final String USER_TOKEN = "dGVzdC11c2VyLXRva2Vu";
    private static final String CLUSTER_URL = "test";

    private static final String CA_CERT_DECODE_TOKEN = "test-ca-cert-token";
    private static final String USER_DECODE_TOKEN = "test-user-token";

    private static HashMap gResponseMapModel = null;
    private static Map gMapModel = null;
    private static AccessToken gFinalResultObject = null;
    private static AccessToken gFinalResultModel = null;

    private static Params gParams = null;
    private static Params gFinalResultParams = null;

    private static Clusters gResultClusters = null;
    private static Clusters gDetailClusters = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    PropertyService propertyService;

    @Mock
    CommonService commonService;

    @Mock
    VaultService vaultService;

    @InjectMocks
    AccessTokenService accessTokenService;

    @Before
    public void setUp() throws Exception {
        gFinalResultModel = new AccessToken();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultModel.setCaCertToken(CA_CERT_DECODE_TOKEN);
        gFinalResultModel.setUserAccessToken(USER_DECODE_TOKEN);

        gFinalResultObject = new AccessToken();
        gFinalResultObject.setCaCertToken(CA_CERT_DECODE_TOKEN);
        gFinalResultObject.setUserAccessToken(USER_DECODE_TOKEN);

        gMapModel = new LinkedHashMap();
        gMapModel.put("ca.crt", CA_CERT_TOKEN);
        gMapModel.put("token", USER_TOKEN);

        gParams = new Params();
        gParams.setCluster(NAME);
        gParams.setUserType(Constants.AUTH_SUPER_ADMIN);

        gResponseMapModel = new HashMap();
        gResponseMapModel.put("data", gMapModel);

        gFinalResultParams = new Params();
        gResultClusters = new Clusters();
        gResultClusters.setClusterApiUrl(CLUSTER_URL);
        gDetailClusters = new Clusters();
        gDetailClusters.setClusterToken(USER_TOKEN);

        gFinalResultParams.setClusterApiUrl(CLUSTER_URL);
        gFinalResultParams.setCluster(NAME);
        gFinalResultParams.setClusterToken(USER_TOKEN);
    }

    @Test
    public void getVaultSecrets() {
        Assert.hasText(gParams.getCluster(), "cluster id not null");
        Assert.hasText(gParams.getUserType(), "userType must valid");

        String clusterId = gParams.getCluster();

        when(vaultService.getClusterDetails(clusterId)).thenReturn(gResultClusters);
        when(vaultService.getClusterInfoDetails(gParams)).thenReturn(gDetailClusters);
        gParams.setCluster(clusterId);
        gParams.setClusterApiUrl(gResultClusters.getClusterApiUrl());

        Params result = accessTokenService.getVaultSecrets(gParams);

        assertEquals(result.getCluster(), gFinalResultParams.getCluster());
        assertEquals(result.getClusterApiUrl(), gFinalResultParams.getClusterApiUrl());
        assertEquals(result.getClusterToken(), gFinalResultParams.getClusterToken());
    }
}