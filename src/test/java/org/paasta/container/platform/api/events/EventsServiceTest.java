package org.paasta.container.platform.api.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
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

    private static HashMap gResultMap = null;

    private static EventsList gResultListModel = null;
    private static EventsList gFinalResultListModel = null;

    private static EventsListAdmin gResultListAdminModel = null;
    private static EventsListAdmin gFinalResultListAdminModel = null;

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

        gResultListAdminModel = new EventsListAdmin();
        gFinalResultListAdminModel = new EventsListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Events 목록 조회(Get Events list) Test
     */
    @Test
    public void getEventsList_Valid_ReturnModel() {
        // given
        TYPE = "node";
        when(eventsServiceMock.generateFieldSelector(TYPE, RESOURCE_UID)).thenReturn("&fieldSelector=involvedObject.name=" + RESOURCE_UID);
        when(propertyService.getCpMasterApiListEventsListAllNamespacesUrl()).thenReturn("/api/v1/events");
        when(eventsServiceMock.generateCpMasterApiListEventsList(TYPE, RESOURCE_UID)).thenReturn("/api/v1/events");
        when(eventsServiceMock.generateLimitParam()).thenReturn("?limit=" + Constants.EVENT_DEFAULT_LIMIT);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/events?limit=5&fieldSelector=involvedObject.name=" + RESOURCE_UID, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        EventsList resultList = (EventsList) eventsService.getEventsList(NAMESPACE, RESOURCE_UID, TYPE);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * Resource 의 Events Admin 목록 조회(Get Events Admin list) Test
     */
    @Test
    public void getEventsListAdmin_Valid_ReturnModel() {
        // given
        TYPE = "node";
        when(eventsServiceMock.generateFieldSelector(TYPE, RESOURCE_UID)).thenReturn("&fieldSelector=involvedObject.name=" + RESOURCE_UID);
        when(propertyService.getCpMasterApiListEventsListAllNamespacesUrl()).thenReturn("/api/v1/events");
        when(eventsServiceMock.generateCpMasterApiListEventsList(TYPE, RESOURCE_UID)).thenReturn("/api/v1/events");
        when(eventsServiceMock.generateLimitParam()).thenReturn("?limit=" + Constants.EVENT_DEFAULT_LIMIT);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/events?limit=5&fieldSelector=involvedObject.name=" + RESOURCE_UID, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        // when
        EventsListAdmin resultList = (EventsListAdmin) eventsService.getEventsListAdmin(NAMESPACE, RESOURCE_UID, TYPE);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace) Test
     */
    @Test
    public void getNamespaceEventsList_Valid_ReturnModel() {
        // given
        TYPE = "node";
        when(propertyService.getCpMasterApiListEventsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/events");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/events?limit=5", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        EventsList resultList = (EventsList) eventsService.getNamespaceEventsList(NAMESPACE);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace) Test
     */
    @Test
    public void getNamespaceEventsListAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListEventsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/events");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/events?limit=5", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, EventsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        // when
        EventsListAdmin resultList = (EventsListAdmin) eventsService.getNamespaceEventsListAdmin(NAMESPACE);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * Field Selector Parameter 생성 (Generate Field Selector Parameter) Test
     * TYPE = null
     */
    @Test
    public void generateFieldSelector_Uid_Valid_ReturnModel() {
        // given
        TYPE = null;
        String fieldSelector = "&fieldSelector=involvedObject.uid=" + RESOURCE_UID;

        // when
        String result = eventsService.generateFieldSelector(TYPE, RESOURCE_UID);

        // then
        assertEquals(fieldSelector, result);
    }

    /**
     * Field Selector Parameter 생성 (Generate Field Selector Parameter) Test
     * TYPE = node
     */
    @Test
    public void generateFieldSelector_Node_Valid_ReturnModel() {
        // given
        TYPE = "node";
        String fieldSelector = "&fieldSelector=involvedObject.name=" + RESOURCE_UID;

        // when
        String result = eventsService.generateFieldSelector(TYPE, RESOURCE_UID);

        // then
        assertEquals(fieldSelector, result);
    }

    /**
     * Node와 타 리소스의 Event 목록 조회 Endpoint 구분 (Separate Endpoints  from Nodes and Other Resources) Test
     * TYPE = null
     */
    @Test
    public void generateCpMasterApiListEventsList_Uid_Valid_ReturnModel() {
        // given
        TYPE = null;
        when(propertyService.getCpMasterApiListEventsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/events");
        String cpMasterApiListEventsList = "/api/v1/namespaces/{namespace}/events";

        // when
        String result = eventsService.generateCpMasterApiListEventsList(TYPE, RESOURCE_UID);

        // then
        assertEquals(cpMasterApiListEventsList, result);
    }

    /**
     * Node와 타 리소스의 Event 목록 조회 Endpoint 구분 (Separate Endpoints  from Nodes and Other Resources) Test
     * TYPE = node
     */
    @Test
    public void generateCpMasterApiListEventsList_Node_Valid_ReturnModel() {
        // given
        TYPE = "node";
        when(propertyService.getCpMasterApiListEventsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/events");
        when(propertyService.getCpMasterApiListEventsListAllNamespacesUrl()).thenReturn("/api/v1/events");
        String cpMasterApiListEventsList = "/api/v1/events";

        // when
        String result = eventsService.generateCpMasterApiListEventsList(TYPE, RESOURCE_UID);

        // then
        assertEquals(cpMasterApiListEventsList, result);
    }

    @Test
    public void generateLimitParam_Valid_ReturnModel() {
        // given
        String limitParam = "?limit=" + Constants.EVENT_DEFAULT_LIMIT;

        // when
        String result = eventsService.generateLimitParam();

        // then
        assertEquals(limitParam, result);
    }
}