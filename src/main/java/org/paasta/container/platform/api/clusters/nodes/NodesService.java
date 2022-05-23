package org.paasta.container.platform.api.clusters.nodes;

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
 * Nodes Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
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
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the nodes list
     */
    public NodesList getNodesList(int offset, int limit, String orderBy, String order, String searchName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesListUrl(),
                HttpMethod.GET, null, Map.class);

        NodesList nodesList = commonService.setResultObject(responseMap, NodesList.class);
        nodesList = commonService.resourceListProcessing(nodesList, offset, limit, orderBy, order, searchName, NodesList.class);
        return (NodesList) commonService.setResultModel(nodesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Nodes Admin 목록 조회(Get Nodes Admin list)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the nodes admin list
     */
    public Object getNodesListAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesListUrl(),
                HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        NodesListAdmin nodesListAdmin = commonService.setResultObject(responseMap, NodesListAdmin.class);
        nodesListAdmin = commonService.resourceListProcessing(nodesListAdmin, offset, limit, orderBy, order, searchName, NodesListAdmin.class);

        return commonService.setResultModel(nodesListAdmin, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Nodes 상세 조회(Get Nodes detail)
     *
     * @param resourceName the resource name
     * @return the nodes detail
     */
    public Nodes getNodes(String resourceName) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesGetUrl()
                        .replace("{name}", resourceName)
                , HttpMethod.GET, null, Map.class);

        return (Nodes) commonService.setResultModel(commonService.setResultObject(responseMap, Nodes.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * NodesAdmin 상세 조회(Get Nodes Admin detail)
     *
     * @param resourceName the resource name
     * @return the nodes admin detail
     */
    public Object getNodesAdmin(String resourceName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesGetUrl()
                        .replace("{name}", resourceName)
                , HttpMethod.GET, null, Map.class);

        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }

        NodesAdmin nodesAdmin = commonService.setResultObject(responseMap, NodesAdmin.class);
        nodesAdmin = commonService.annotationsProcessing(nodesAdmin, NodesAdmin.class);
        return commonService.setResultModel(nodesAdmin,Constants.RESULT_STATUS_SUCCESS);
    }

}
