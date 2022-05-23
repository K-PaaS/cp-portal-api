package org.paasta.container.platform.api.adminToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class AdminTokenServiceTest {
    private static final String TOKEN_NAME = Constants.TOKEN_KEY;
    private static final String TOKEN_VALUE = "test-token-value";

    private static AdminToken gParamModel = null;
    private static AdminToken gFinalResultModel = null;

    @Mock
    RestTemplateService restTemplateService;

    @InjectMocks
    AdminTokenService adminTokenService;

    @Before
    public void setUp() {
        gFinalResultModel = new AdminToken();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultModel.setTokenName(TOKEN_NAME);
        gFinalResultModel.setTokenValue(TOKEN_VALUE);

        gParamModel = new AdminToken();
        gParamModel.setTokenName(TOKEN_NAME);
        gParamModel.setTokenValue(TOKEN_VALUE);
    }

    @Test
    public void getSecrets() {

        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/adminToken", HttpMethod.POST, gParamModel, AdminToken.class))
                .thenReturn(gFinalResultModel);

        AdminToken adminToken = adminTokenService.saveAdminToken(TOKEN_VALUE);

        assertThat(adminToken).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, adminToken.getResultCode());
    }
}