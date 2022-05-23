package org.paasta.container.platform.api.events;

import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Events Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.05
 */
@Service
public class EventsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Events service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public EventsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Events 목록 조회(Get Events list)
     *
     * @param namespace   the namespace
     * @param resourceUid the resourceUid
     * @param type        the type
     * @return the events list
     */
    public Object getEventsList(String namespace, String resourceUid, String type) {

        HashMap responseMap = null;

        String fieldSelector = generateFieldSelector(type, resourceUid);
        String cpMasterApiListEventsListUrl = generateCpMasterApiListEventsList(type, resourceUid);

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                cpMasterApiListEventsListUrl.replace("{namespace}", namespace) + generateLimitParam() + fieldSelector, HttpMethod.GET, null, Map.class);
        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);
        return commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Resource 의 Events Admin 목록 조회(Get Events Admin list)
     *
     * @param namespace   the namespace
     * @param resourceUid the resourceUid
     * @param type        the type
     * @return the events list
     */
    public Object getEventsListAdmin(String namespace, String resourceUid, String type) {
        HashMap responseMap = null;

        String fieldSelector = generateFieldSelector(type, resourceUid);
        String cpMasterApiListEventsListUrl = generateCpMasterApiListEventsList(type, resourceUid);

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                cpMasterApiListEventsListUrl.replace("{namespace}", namespace) + generateLimitParam() + fieldSelector, HttpMethod.GET, null, Map.class);


        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        EventsListAdmin eventsListAdmin = commonService.setResultObject(responseMap, EventsListAdmin.class);
        return commonService.setResultModel(eventsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)
     *
     * @param namespace the namespace
     * @return the events list
     */
    public Object getNamespaceEventsList(String namespace) {
        HashMap responseMap = null;

        Object response = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListEventsListUrl().replace("{namespace}", namespace) + generateLimitParam()
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);
        return commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 특정 Namespace 의 전체 Events Admin 목록 조회(Get Events Admin list in a Namespace)
     *
     * @param namespace the namespace
     * @return the events list
     */
    public Object getNamespaceEventsListAdmin(String namespace) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListEventsListUrl().replace("{namespace}", namespace) + generateLimitParam()
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        EventsListAdmin eventsListAdmin = commonService.setResultObject(responseMap, EventsListAdmin.class);
        return commonService.setResultModel(eventsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Field Selector Parameter 생성 (Generate Field Selector Parameter)
     *
     * @param resourceUid the namespace
     * @return the String
     */
    public String generateFieldSelector(String type, String resourceUid) {

        // uid
        String fieldSelector = "&fieldSelector=involvedObject.uid=" + resourceUid;

        if (type != null) {
            // node
            fieldSelector = "&fieldSelector=involvedObject.name=" + resourceUid;
        }

        return fieldSelector;
    }

    /**
     * Node 와 타리소스의 Event 목록 조회 Endpoint 구분 (Separate Endpoints from Nodes and Other Resources)
     *
     * @param resourceUid the namespace
     * @return the String
     */
    public String generateCpMasterApiListEventsList(String type, String resourceUid) {

        String cpMasterApiListEventsList = propertyService.getCpMasterApiListEventsListUrl();

        if (type != null) {
            // node
            cpMasterApiListEventsList = propertyService.getCpMasterApiListEventsListAllNamespacesUrl();
        }

        return cpMasterApiListEventsList;
    }


    public String generateLimitParam() {
        return "?limit=" + Constants.EVENT_DEFAULT_LIMIT;
    }
}
