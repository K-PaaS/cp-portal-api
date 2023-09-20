package org.container.platform.api.clusters.nodes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class NodesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String NODE_NAME = "test-node-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";

    private static HashMap gResultMap = null;

    private static NodesList gResultListModel = null;
    private static NodesList gFinalResultListModel = null;
    private static NodesList gFinalResultListFailModel = null;

    private static Nodes gResultModel = null;
    private static Nodes gFinalResultModel = null;

    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    NodesService nodesService;

    @Before
    public void setUp() throws Exception {
        gResultMap = new HashMap();
        gParams = new Params();

        gResultStatusModel = new ResultStatus();
        gResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultStatusModel = new ResultStatus();
        gFinalResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_CLUSTER_NODES);

        // 리스트가져옴
        gResultListModel = new NodesList();

        gFinalResultListModel = new NodesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new NodesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Nodes();
        gFinalResultModel = new Nodes();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gFinalResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        CommonMetaData commonMetaData = new CommonMetaData();
        Map<String, String> annotations = new HashMap<>();
        annotations.put(KUBE_ANNOTATIONS, KUBE_ANNOTATIONS);
        commonMetaData.setAnnotations(annotations);

        CommonAnnotations commonAnnotations = new CommonAnnotations();
        commonAnnotations.setCheckYn("Y");
        commonAnnotations.setKey(KUBE_ANNOTATIONS);
        commonAnnotations.setValue(KUBE_ANNOTATIONS);

        List<CommonAnnotations> commonAnnotationsList = new ArrayList<>();
        commonAnnotationsList.add(commonAnnotations);

        //Params setting
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);
        gParams.setOffset(OFFSET);
        gParams.setLimit(LIMIT);
        gParams.setOrderBy(ORDER_BY);
        gParams.setOrder(ORDER);
        gParams.setSearchName(SEARCH_NAME);
        gParams.setResourceName(NODE_NAME);
        gParams.setYaml(YAML_STRING);
    }

    @Test
    public void getNodesList() {
        //when
        when(propertyService.getCpMasterApiListNodesListUrl()).thenReturn("/api/v1/nodes");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, NodesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, NodesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        NodesList resultList = nodesService.getNodesList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getNodes() {
        //when
        when(propertyService.getCpMasterApiListNodesGetUrl()).thenReturn("/api/v1/nodes/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Nodes.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, Nodes.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Nodes result = nodesService.getNodes(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


}