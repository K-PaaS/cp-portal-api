package org.paasta.container.platform.api.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import org.paasta.container.platform.api.clusters.nodes.Nodes;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.common.model.CommonCondition;
import org.paasta.container.platform.api.endpoints.support.EndPointsDetailsItem;
import org.paasta.container.platform.api.endpoints.support.EndpointAddress;
import org.paasta.container.platform.api.endpoints.support.EndpointPort;
import org.paasta.container.platform.api.endpoints.support.EndpointSubset;

/**
 * Endpoints Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class EndpointsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final NodesService nodesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Endpoints service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public EndpointsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, NodesService nodesService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.nodesService = nodesService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * Endpoints 상세 조회(Get Endpoints detail)
     *
     * @param params the params
     * @return the endpoints detail
     */
    public Object getEndpoints(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListEndpointsGetUrl(), HttpMethod.GET, null, Map.class, params);

        Endpoints endpoints = commonService.setResultObject(responseMap, Endpoints.class);

        if (endpoints.getSubsets() == null) {
            List<EndpointSubset> subset = new ArrayList<>();
            endpoints.setSubsets(subset);
        }
        endpoints = endpointsProcessing(endpoints, params);
        return (Endpoints) commonService.setResultModel(endpoints, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Node 명에 따른 Node "Ready" 상태 값 조회 (Get Node "Ready" Status Value by Node Name)
     *
     * @return the endpoints
     */
    public Endpoints endpointsProcessing(Endpoints endpoints, Params params) {

        List<EndPointsDetailsItem> endPointsDetailsItemsList = new ArrayList<>();

        List<EndpointSubset> susbsets = endpoints.getSubsets();

        for (EndpointSubset es : susbsets) {

            List<EndpointAddress> addresses = es.getAddresses();
            List<EndpointPort> ports = es.getPorts();

            if (addresses == null) {
                addresses = es.getNotReadyAddresses();
            }

            if (ports != null) {

                for (EndpointAddress endpointAddress : addresses) {

                    EndPointsDetailsItem endPointsDetailsItem = new EndPointsDetailsItem();
                    String nodeName = CommonUtils.resourceNameCheck(endpointAddress.getNodeName());

                    for (EndpointPort endpointPort : ports) {
                        endpointPort.setName(CommonUtils.resourceNameCheck(endpointPort.getName()));
                    }

                    String nodeReady = Constants.noName;

                    if (!nodeName.equals(Constants.noName)) {
                        params.setResourceName(nodeName);
                        Nodes nodesDetails = nodesService.getNodes(params);

                        List<CommonCondition> nodeConditionList = nodesDetails.getStatus().getConditions();

                        for (CommonCondition condition : nodeConditionList) {
                            if (condition.getType().equals(Constants.STRING_CONDITION_READY)) {
                                nodeReady = condition.getStatus();
                            }
                        }
                    }

                    endPointsDetailsItem.setHost(endpointAddress.getIp());
                    endPointsDetailsItem.setPorts(ports);
                    endPointsDetailsItem.setNodes(nodeName);
                    endPointsDetailsItem.setReady(nodeReady);

                    endPointsDetailsItemsList.add(endPointsDetailsItem);

                }
            }

        }

        Endpoints returnEndpoints = new Endpoints();
        returnEndpoints.setEndpoints(endPointsDetailsItemsList);

        return returnEndpoints;
    }


}
