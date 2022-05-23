package org.paasta.container.platform.api.accessInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.roles.RolesList;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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

    private static final String CA_CERT_DECODE_TOKEN = "test-ca-cert-token";
    private static final String USER_DECODE_TOKEN = "test-user-token";

    private static HashMap gResponseMapModel = null;
    private static Map gMapModel = null;
    private static AccessToken gFinalResultObject = null;
    private static AccessToken gFinalResultModel = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    PropertyService propertyService;

    @Mock
    CommonService commonService;

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

        gResponseMapModel = new HashMap();
        gResponseMapModel.put("data", gMapModel);
    }

    @Test
    public void getSecrets() {
        when(propertyService.getCpMasterApiListSecretsGetUrl())
                .thenReturn("/api/v1/namespaces/{namespace}/secrets/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/secrets/" + NAME, HttpMethod.GET, null, Map.class))
                .thenReturn(gResponseMapModel);
        when(commonService.setResultObject(gFinalResultObject, AccessToken.class)).thenReturn(gFinalResultObject);
        when(commonService.setResultModel(gFinalResultObject, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        AccessToken accessToken = accessTokenService.getSecrets(NAMESPACE, NAME);

        assertThat(accessToken).isNotNull();
        assertEquals(gFinalResultModel, accessToken);
    }
}