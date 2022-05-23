package org.paasta.container.platform.api.privateRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class PrivateRegistryServiceTest {

    private static final String IMAGE_NAME = "registry";
    private static PrivateRegistry gFinalResultModel = null;
    private static ResultStatus gResultFailStatusModel = null;
    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @InjectMocks
    PrivateRegistryService privateRegistryService;

    @Before
    public void setUp(){
        gFinalResultModel = new PrivateRegistry();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultModel.setDetailMessage(CommonStatusCode.OK.getMsg());


        gResultFailStatusModel = new ResultStatus();
        gResultFailStatusModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailStatusModel.setResultMessage(Constants.RESULT_STATUS_FAIL);

    }
    @Test
    public void getPrivateRegistry() {

        when(restTemplateService.sendAdmin(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_PRIVATE_REGISTRY
                .replace("{imageName:.+}", IMAGE_NAME), HttpMethod.GET, null, PrivateRegistry.class)).thenReturn(gFinalResultModel);
        when(commonService.setResultObject(gFinalResultModel, PrivateRegistry.class)).thenReturn(gFinalResultModel);
        when(commonService.setResultModel(gFinalResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
        PrivateRegistry privateRegistry = (PrivateRegistry) privateRegistryService.getPrivateRegistry(IMAGE_NAME);

        assertEquals(Constants.RESULT_STATUS_SUCCESS, privateRegistry.getResultCode());
    }

    @Test
    public void getPrivateRegistry_Not_Found_Result_Status() {

        when(restTemplateService.sendAdmin(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_PRIVATE_REGISTRY
                .replace("{imageName:.+}", IMAGE_NAME), HttpMethod.GET, null, ResultStatus.class)).thenReturn(gResultFailStatusModel);

        assertEquals(Constants.RESULT_STATUS_FAIL, gResultFailStatusModel.getResultCode());
    }
}