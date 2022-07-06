package org.paasta.container.platform.api.metrics;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.roles.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Metrics Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.07.04
 */
@Service
public class MetricsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResultStatusService resultStatusService;


    /**
     * Instantiates a new Roles service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public MetricsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Metrics Node 목록 조회(Get Metrics for Nodes)
     *
     * @param params the params
     * @return the roles list
     */
    public MetricsList getMetricsNodesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsNodeListUrl(), HttpMethod.GET, null, Map.class, params);
        MetricsList nodeMetricsList = commonService.setResultObject(responseMap, MetricsList.class);
        return (MetricsList) commonService.setResultModel(nodeMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles 상세 조회(Get Roles detail)
     *
     * @param params the params
     * @return the roles
     */
    public Roles getRoles(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesGetUrl(), HttpMethod.GET, null, Map.class, params);
        Roles roles = commonService.setResultObject(responseMap, Roles.class);
        roles = commonService.annotationsProcessing(roles, Roles.class);
        return (Roles) commonService.setResultModel(roles, Constants.RESULT_STATUS_SUCCESS);
    }




    public MetricsItems findNodeMetric(String nodeName, MetricsList list) {
        for (MetricsItems metric : list.getItems()) {
            if (metric.getName().equals(nodeName)) {
                return metric;
            }
        }
        return null;
    }

}