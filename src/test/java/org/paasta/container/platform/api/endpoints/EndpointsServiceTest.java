package org.paasta.container.platform.api.endpoints;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.nodes.Nodes;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.clusters.nodes.support.NodesStatus;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonCondition;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.endpoints.support.EndPointsDetailsItem;
import org.paasta.container.platform.api.endpoints.support.EndpointAddress;
import org.paasta.container.platform.api.endpoints.support.EndpointPort;
import org.paasta.container.platform.api.endpoints.support.EndpointSubset;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class EndpointsServiceTest {
    private static final String NAMESPACE = "cp-namespace";
    private static final String ENDPOINTS_NAME = "test-service";

    private static HashMap gResultMap = null;

    private static Endpoints gResultModel = null;
    private static Endpoints gFinalResultModel = null;

    private static Endpoints gResultAdminModel = null;
    private static Endpoints gFinalResultAdminModel = null;

    private static List<EndPointsDetailsItem> endpoints =null;
    private static EndpointSubset gResultSubsetModel = null;
    private static EndpointSubset gFinalResultSubsetModel = null;

    private static List<EndpointAddress> gResultListEndpointAddressModel = null;

    private static List<EndpointSubset> gResultSubsetListModel;

    private static Nodes gResultNodeAdminModel =null;
    private static Params gParams = null;
    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Mock
    NodesService nodesService;

    @Mock
    EndpointsService endpointsServiceMock;

    @Mock
    ResultStatusService resultStatusService;

    @InjectMocks
    EndpointsService endpointsService;

    @Before
    public void setUp() {
        gParams = new Params();
        gResultMap = new HashMap();

        gResultModel = new Endpoints();
        gFinalResultModel = new Endpoints();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultAdminModel = new Endpoints();
        endpoints = new ArrayList<>();
        gResultAdminModel.setEndpoints(endpoints);

        List<EndpointSubset> epsbList = new ArrayList<>();
        epsbList.add(new EndpointSubset());

        gResultAdminModel.setSubsets(epsbList);

        gFinalResultAdminModel = new Endpoints();

        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setSubsets(epsbList);

        gResultSubsetListModel = new ArrayList<>();

        gResultNodeAdminModel = new Nodes();
        gResultNodeAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultAdminModel = new Endpoints();
        gResultAdminModel.setResultCode("SUCCESS");
        gResultAdminModel.setHttpStatusCode(200);

        List<EndpointAddress> addresses = new ArrayList<>();
        EndpointAddress endpointAddress = new EndpointAddress();
        endpointAddress.setIp("10.244.1.11");
        endpointAddress.setNodeName("paasta-cp-k8s-worker-001");
        endpointAddress.setHostname("");
        addresses.add(endpointAddress);

        List<EndpointPort> ports = new ArrayList<>();
        EndpointPort endpointPort = new EndpointPort();
        endpointPort.setName("http");
        endpointPort.setPort(80);
        endpointPort.setProtocol("TCP");
        ports.add(endpointPort);

        List<EndpointSubset> subsets = new ArrayList<>();
        EndpointSubset endpointSubset = new EndpointSubset();
        endpointSubset.setAddresses(addresses);
        endpointSubset.setPorts(ports);
        endpointSubset.setNotReadyAddresses(addresses);

        subsets.add(endpointSubset);
        gResultAdminModel.setSubsets(subsets);

        String nodeName = "paasta-cp-k8s-worker-001";
        gParams.setNodeName(nodeName);

        Nodes nodesDetails = new Nodes();
        nodesDetails.setResultCode("SUCCESS");


        NodesStatus nodesStatus = new NodesStatus();
        List<CommonCondition> conditions = new ArrayList<>();

        CommonCondition commonCondition = new CommonCondition();
        commonCondition.setType("Ready");
        commonCondition.setStatus("True");
        conditions.add(commonCondition);
        nodesStatus.setConditions(conditions);
        nodesDetails.setStatus(nodesStatus);

        when(nodesService.getNodes(gParams)).thenReturn(nodesDetails);


        List<EndPointsDetailsItem> endPointsDetailsItemAdminsList = new ArrayList<>();
        EndPointsDetailsItem endPointsDetailsItem = new EndPointsDetailsItem();
        endPointsDetailsItem.setHost("10.244.1.11");
        endPointsDetailsItem.setPorts(ports);
        endPointsDetailsItem.setNodes(nodeName);
        endPointsDetailsItem.setReady("True");
        endPointsDetailsItemAdminsList.add(endPointsDetailsItem);

        gResultAdminModel.setEndpoints(endPointsDetailsItemAdminsList);

        gFinalResultAdminModel.setEndpoints(endPointsDetailsItemAdminsList);
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Endpoints 상세 조회(Get Endpoints list) Test
     * (User Portal)
     */
    @Test
    public void getEndpoints_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListEndpointsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/endpoints/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListEndpointsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Endpoints.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
        when(resultStatusService.NOT_FOUND_RESULT_STATUS()).thenReturn(null);

        // when
        Object result = endpointsService.getEndpoints(gParams);

        // then
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Node 명에 따른 Node "Ready" 상태 값 조회 (Get Node "Ready" Status Value by Node Name) Test
     */
//    @Test
//    public void endpointsAdminProcessing_Valid_ReturnModel() {
//
//        Endpoints result = endpointsService.endpointsProcessing(gResultModel, gParams);
//
////        assertEquals(null, result.getResultCode());
//
//    }
    @Test
    public void endpointsProcessing() {
        List<EndPointsDetailsItem> endPointsDetailsItemsList = new ArrayList<>();

        List<EndpointSubset> susbsets = new ArrayList<>();
        EndpointSubset subset = new EndpointSubset();
        List<EndpointAddress> addressesList = new ArrayList<>();
        EndpointAddress address = new EndpointAddress();
        addressesList.add(address);
        subset.setAddresses(addressesList);
        EndpointPort port = new EndpointPort();
        List<EndpointPort> portList = new ArrayList<>();
        portList.add(port);
        subset.setPorts(portList);

        susbsets.add(subset);

        gResultModel.setSubsets(susbsets);



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
                        gParams.setResourceName(nodeName);
                        Nodes nodesDetails = nodesService.getNodes(gParams);

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


        Endpoints result = endpointsService.endpointsProcessing(gResultModel, gParams);
    }
}