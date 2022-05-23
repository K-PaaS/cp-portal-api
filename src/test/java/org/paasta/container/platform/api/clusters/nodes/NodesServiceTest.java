package org.paasta.container.platform.api.clusters.nodes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.*;
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
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;

    private static HashMap gResultMap = null;
    private static HashMap gResultAdminMap = null;
    private static HashMap gResultAdminFailMap = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static Nodes gResultModel = null;
    private static Nodes gFinalResultModel = null;

    private static NodesAdmin gResultAdminModel = null;
    private static NodesAdmin gFinalResultAdminModel = null;
    private static NodesAdmin gFinalResultAdminFailModel = null;

    private static NodesList gResultListModel = null;
    private static NodesList gFinalResultListModel = null;
    private static NodesList gFinalResultListFailModel = null;

    private static NodesListAdmin gResultListAdminModel = null;
    private static NodesListAdmin gFinalResultListAdminModel = null;
    private static NodesListAdmin gFinalResultListAdminFailModel = null;

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

        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new NodesListAdmin();
        gFinalResultListAdminModel = new NodesListAdmin();

        gFinalResultListAdminModel = new NodesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new NodesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        // 하나만 가져옴
        gResultAdminModel = new NodesAdmin();
        gFinalResultAdminModel = new NodesAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());


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
        gResultAdminModel.setAnnotations(commonAnnotationsList);


        gFinalResultAdminFailModel = new NodesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
    }

    @Test
    public void getNodesList() {
        //when
        when(propertyService.getCpMasterApiListNodesListUrl()).thenReturn("/api/v1/nodes");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, NodesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, NodesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        NodesList resultList = nodesService.getNodesList(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getNodesListAdmin() {
        //when
        when(propertyService.getCpMasterApiListNodesListUrl()).thenReturn("/api/v1/nodes");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, NodesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, NodesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        NodesListAdmin resultList = (NodesListAdmin) nodesService.getNodesListAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getNodes() {
        //when
        when(propertyService.getCpMasterApiListNodesGetUrl()).thenReturn("/api/v1/nodes/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes/" + NODE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Nodes.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Nodes result = nodesService.getNodes(NODE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getNodesAdmin() {
        //when
        when(propertyService.getCpMasterApiListNodesGetUrl()).thenReturn("/api/v1/nodes/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/nodes/" + NODE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, NodesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, NodesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        NodesAdmin result = (NodesAdmin) nodesService.getNodesAdmin(NODE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

}