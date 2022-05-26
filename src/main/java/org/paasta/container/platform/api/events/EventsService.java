package org.paasta.container.platform.api.events;

import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Events Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
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
     * Resource 의 Events 목록 조회(Get Events list)
     *
     * @param params the params
     * @return the events list
     */
    public EventsList getEventsList(Params params) {
        String fieldSelector = generateFieldSelector(params.getType(), params.getOwnerReferencesUid());
        String cpMasterApiListEventsListUrl = generateCpMasterApiListEventsList(params.getType());

        HashMap responseMap =  (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API, cpMasterApiListEventsListUrl
                + generateLimitParam() + fieldSelector, HttpMethod.GET, null, Map.class, params);
        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);
        return (EventsList) commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)
     *
     * @param params the params
     * @return the events list
     */
    public EventsList getNamespaceEventsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListEventsListUrl(), HttpMethod.GET, null, Map.class, params);
        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);
        return (EventsList) commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Field Selector Parameter 생성 (Generate Field Selector Parameter)
     *
     * @param ownerReferencesUid the owner references uid
     * @return the String
     */
    public String generateFieldSelector(String type, String ownerReferencesUid) {

        // uid
        String fieldSelector = "&fieldSelector=involvedObject.uid=" + ownerReferencesUid;

        if (type != null) {
            // node
            fieldSelector = "&fieldSelector=involvedObject.name=" + ownerReferencesUid;
        }

        return fieldSelector;
    }

    /**
     * Node 와 타리소스의 Event 목록 조회 Endpoint 구분 (Separate Endpoints from Nodes and Other Resources)
     *
     * @param type the type
     * @return the String
     */
    public String generateCpMasterApiListEventsList(String type) {

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
