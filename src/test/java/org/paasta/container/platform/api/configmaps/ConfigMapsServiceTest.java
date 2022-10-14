package org.paasta.container.platform.api.configmaps;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ConfigMapsServiceTest {

    @Mock
    RestTemplateService restTemplateService;
    @Mock
    CommonService commonService;
    @Mock
    PropertyService propertyService;

    @InjectMocks
    ConfigMapsService configMapsService;

    private static final String YAML_STRING = "test-string";

    private static Params gParams = null;
    private static HashMap gResultMap = null;
    private static ConfigMapsList gResultListModel = null;
    private static ConfigMaps gResultModel = null;
    private static CommonResourcesYaml gResultYaml = null;
    private static ResultStatus gResultStatus = null;


    @Before
    public void setUp() {
        gParams = new Params();
        gResultMap = new HashMap();
        gResultListModel = new ConfigMapsList();
        gResultModel = new ConfigMaps();
        gResultYaml = new CommonResourcesYaml("");
        gResultStatus = new ResultStatus();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        gResultModel.setMetadata(commonMetaData);


        List<ConfigMaps> itemList = new ArrayList<>();
        itemList.add(gResultModel);
        gResultListModel.setItems(itemList);
    }

    @Test
    public void getConfigMapsList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        procConfigMapsList();

        ConfigMapsList result = configMapsService.getConfigMapsList(gParams);
    }

    @Test
    public void getConfigMaps() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ConfigMaps.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultModel);

        configMapsService.getConfigMaps(gParams);
    }

    @Test
    public void getConfigMapsYaml() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultYaml);

        configMapsService.getConfigMapsYaml(gParams);
    }

    @Test
    public void createConfigMaps() {
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);

        configMapsService.createConfigMaps(gParams);
    }

    @Test
    public void deleteConfigMaps() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);

        configMapsService.deleteConfigMaps(gParams);
    }

    @Test
    public void updateConfigMaps() {
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);

        configMapsService.updateConfigMaps(gParams);
    }

    public void procConfigMapsList() {
        when(commonService.setResultObject(gResultMap, ConfigMapsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, ConfigMapsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultListModel);
    }


}