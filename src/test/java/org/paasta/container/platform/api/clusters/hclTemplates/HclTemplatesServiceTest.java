package org.paasta.container.platform.api.clusters.hclTemplates;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class HclTemplatesServiceTest {

    private static HclTemplates gResultModel = null;
    private static HclTemplates gFinalResultModel = null;

    private static HclTemplatesList gResultListModel = null;
    private static HclTemplatesList gFinalResultListModel = null;


    private static Params gParams = null;


    @Mock
    private RestTemplateService restTemplateService;
    @Mock
    private CommonService commonService;
    @InjectMocks
    private HclTemplatesService hclTemplatesService;


    @Before
    public void setUp() {
        gParams = new Params();
        gParams.setProviderType(Constants.ProviderType.AWS);

        gResultModel = new HclTemplates();
        gFinalResultModel = new HclTemplates();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        List<HclTemplates> listItemModel = new ArrayList<>();
        listItemModel.add(gResultModel);

        gResultListModel = new HclTemplatesList();
        gResultListModel.setItems(listItemModel);
        gFinalResultListModel = new HclTemplatesList();
        gFinalResultListModel.setItems(listItemModel);
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getHclTemplatesList() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.GET, null, HclTemplatesList.class, gParams)).thenReturn(gResultListModel);
        when(commonService.globalListProcessing(gResultListModel, gParams, HclTemplatesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        HclTemplatesList result = hclTemplatesService.getHclTemplatesList(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getHclTemplatesListByProvider() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/provider/{providerType:.+}"
                .replace("{providerType:.+}", gParams.getProviderType().name()), HttpMethod.GET, null, HclTemplatesList.class, gParams)).thenReturn(gResultListModel);
        when(commonService.globalListProcessing(gResultListModel, gParams, HclTemplatesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        HclTemplatesList result = hclTemplatesService.getHclTemplatesListByProvider(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getHclTemplates() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/{id:.+}"
                .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.GET, null, HclTemplates.class, gParams)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        HclTemplates result = hclTemplatesService.getHclTemplates(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void createHclTemplates() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.POST, new HclTemplates(), HclTemplates.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        HclTemplates result = hclTemplatesService.createHclTemplates(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void updateHclTemplates() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.PUT, new HclTemplates(), HclTemplates.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        HclTemplates result = hclTemplatesService.updateHclTemplates(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);

    }

    @Test
    public void deleteHclTemplates() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/{id:.+}"
                        .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.DELETE, new HclTemplates(), HclTemplates.class, gParams),
                Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        HclTemplates result = hclTemplatesService.deleteHclTemplates(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }
}