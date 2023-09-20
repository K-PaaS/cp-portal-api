package org.container.platform.api.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class EventsServiceTest {
    private static final String NAMESPACE = "cp-namespace";

    private static String TYPE = "node";
    private static final String RESOURCE_UID = "";
    private static String URL_STRING = "test.com";

    private static HashMap gResultMap = null;

    private static EventsList gResultListModel = null;
    private static EventsList gFinalResultListModel = null;

    private static EventsList gResultListAdminModel = null;
    private static EventsList gFinalResultListAdminModel = null;
    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Mock
    EventsService eventsServiceMock;

    @InjectMocks
    EventsService eventsService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();

        gResultListModel = new EventsList();
        gFinalResultListModel = new EventsList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultListAdminModel = new EventsList();
        gFinalResultListAdminModel = new EventsList();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gParams = new Params();
        gParams.setType(TYPE);
    }


    @Test
    public void getEventsList() {
        when(propertyService.getCpMasterApiListEventsListUrl() + "?fieldSelector=involvedObject.uid=" + gParams.getResourceUid()).thenReturn(URL_STRING);

        when(propertyService.getCpMasterApiListEventsListAllNamespacesUrl() + "?fieldSelector=involvedObject.name=" + gParams.getResourceUid()).thenReturn(URL_STRING);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, URL_STRING, HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsList.class)).thenReturn(gResultListModel);
        // 이벤트 메세지 최신 10개만 limit
        gParams.setLimit(10);
        gParams.setOffset(0);
        when(commonService.resourceListProcessing(gResultListModel, gParams, EventsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        eventsService.getEventsList(gParams);
    }

    @Test
    public void getNamespaceEventsList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListEventsListUrl(),
                HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsList.class)).thenReturn(gResultListModel);

        // 이벤트 메세지 최신 10개만 limit
        gParams.setLimit(10);
        gParams.setOffset(0);
        when(commonService.resourceListProcessing(gResultListModel, gParams, EventsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultListModel);

        eventsService.getNamespaceEventsList(gParams);
    }

    @Test
    public void generateEventsListUrl() {
        when(propertyService.getCpMasterApiListEventsListUrl() + "?fieldSelector=involvedObject.uid=" + gParams.getResourceUid()).thenReturn(URL_STRING);

        when(propertyService.getCpMasterApiListEventsListAllNamespacesUrl() + "?fieldSelector=involvedObject.name=" + gParams.getResourceUid()).thenReturn(URL_STRING);

        eventsService.generateEventsListUrl(gParams);
    }
}