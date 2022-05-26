package org.paasta.container.platform.api.clusters.nodes;

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
 * Nodes Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class NodesService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Nodes service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public NodesService(RestTemplateService restTemplateService, CommonService commonService,
                        PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Nodes 목록 조회(Get Nodes list)
     *
     * @param params the params
     * @return the nodes list
     */
    public NodesList getNodesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesListUrl(), HttpMethod.GET, null, Map.class, params);
        NodesList nodesList = commonService.setResultObject(responseMap, NodesList.class);
        nodesList = commonService.resourceListProcessing(nodesList, params, NodesList.class);
        return (NodesList) commonService.setResultModel(nodesList, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Nodes 상세 조회(Get Nodes detail)
     *
     * @param params the params
     * @return the nodes detail
     */
    public Nodes getNodes(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesGetUrl(), HttpMethod.GET, null, Map.class, params);
        Nodes nodes = commonService.setResultObject(responseMap, Nodes.class);
        nodes = commonService.annotationsProcessing(nodes, Nodes.class);
        return (Nodes) commonService.setResultModel(nodes,Constants.RESULT_STATUS_SUCCESS);
    }

}
