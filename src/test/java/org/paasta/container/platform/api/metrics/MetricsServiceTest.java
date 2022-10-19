package org.paasta.container.platform.api.metrics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.support.NodesListItem;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonStatus;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.metrics.custom.ContainerMetrics;
import org.paasta.container.platform.api.metrics.custom.Quantity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class MetricsServiceTest {

    private static HashMap gResultMap = null;
    private static NodesMetricsList gResultNodesListModel = null;
    private static PodsMetricsList gResultPodsListModel = null;
    private static NodesMetricsItems gResultNodesModel = null;
    private static Params gParams = null;
    private static NodesList nodesList = null;
    private static NodesMetricsList nodesMetricsList = null;

    @Mock
    RestTemplateService restTemplateService;
    @Mock
    CommonService commonService;
    @Mock
    PropertyService propertyService;

    @InjectMocks
    MetricsService metricsService;


    @Before
    public void setUp() throws Exception {
        gParams = new Params();
        gResultMap = new HashMap();
        gResultNodesListModel = new NodesMetricsList();
        gResultPodsListModel = new PodsMetricsList();
        gResultNodesModel = new NodesMetricsItems();

        nodesList = new NodesList();
        NodesListItem nodesListItem = new NodesListItem();
        CommonStatus commonStatus = new CommonStatus();
        Map<String, Quantity> capacity = new HashMap<>();
        Quantity quantity = new Quantity(BigDecimal.valueOf(10000), Quantity.Format.DECIMAL_SI);
        capacity.put("type", quantity);
        commonStatus.setCapacity(capacity);

        nodesListItem.setStatus(commonStatus);
        nodesListItem.setName("nodeName");
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("nodeName");
        nodesListItem.setMetadata(commonMetaData);
        List<NodesListItem> nodesListItems = new ArrayList<>();
        nodesListItems.add(nodesListItem);
        nodesList.setItems(nodesListItems);

        nodesMetricsList = new NodesMetricsList();
        List<NodesMetricsItems> nodesMetricsItems = new ArrayList<>();
        NodesMetricsItems nodesMetricsItem = new NodesMetricsItems();
        nodesMetricsItem.setName("nodeName");
        nodesMetricsItem.setMetadata(commonMetaData);
        nodesMetricsItem.setUsage(capacity);
        nodesMetricsItems.add(nodesMetricsItem);
        nodesMetricsList.setItems(nodesMetricsItems);
    }

    @Test
    public void getNodesMetricsList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsNodesListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, NodesMetricsList.class)).thenReturn(gResultNodesListModel);
        when(commonService.setResultModel(gResultNodesListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultNodesListModel);

        metricsService.getNodesMetricsList(gParams);
    }

    @Test
    public void getPodsMetricsList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsMetricsList.class)).thenReturn(gResultPodsListModel);
        when(commonService.setResultModel(gResultPodsListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultPodsListModel);

        metricsService.getPodsMetricsList(gParams);
    }

    @Test
    public void findNodeMetric() {
    }

    @Test
    public void findAllNodesAvgUsage() {


        metricsService.findAllNodesAvgUsage(nodesList, nodesMetricsList, "type");
    }

    @Test
    public void podMetricSum() {


    }

    @Test
    public void topPods() {
        PodsMetricsItems podsMetricsItems = new PodsMetricsItems();
        podsMetricsItems.setName("test1");
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("test1");
        metaData.setNamespace("namespace1");
        podsMetricsItems.setMetadata(metaData);
        podsMetricsItems.setNamespace("namespace1");
        List<ContainerMetrics> containerMetricsList = new ArrayList<>();
        ContainerMetrics containerMetrics = new ContainerMetrics();
        Map<String, Quantity> usage = new HashMap<>();
        usage.put("type", new Quantity(BigDecimal.valueOf(10000), Quantity.Format.DECIMAL_SI));
        containerMetrics.setUsage(usage);
        containerMetricsList.add(containerMetrics);
        podsMetricsItems.setContainers(containerMetricsList);

        PodsMetricsItems podsMetricsItems2 = new PodsMetricsItems();
        podsMetricsItems2.setName("test2");
        podsMetricsItems2.setNamespace("namespace2");
        CommonMetaData metaData2 = new CommonMetaData();
        metaData2.setName("test2");
        metaData2.setNamespace("namespace2");
        podsMetricsItems2.setMetadata(metaData2);
        List<ContainerMetrics> containerMetricsList2 = new ArrayList<>();
        ContainerMetrics containerMetrics2 = new ContainerMetrics();
        Map<String, Quantity> usage2 = new HashMap<>();
        usage.put("type", new Quantity(BigDecimal.valueOf(100000), Quantity.Format.DECIMAL_SI));
        containerMetrics2.setUsage(usage);
        containerMetricsList2.add(containerMetrics2);
        podsMetricsItems2.setContainers(containerMetricsList2);

        List<PodsMetricsItems> podsMetricsItemsListFin = new ArrayList<>();
        podsMetricsItemsListFin.add(podsMetricsItems);
        podsMetricsItemsListFin.add(podsMetricsItems2);

        metricsService.topPods(podsMetricsItemsListFin, "type", 2);

    }

    @Test
    public void generatePodsUsageMap() {
    }

    @Test
    public void topNodes() {
        List<NodesListItem> nodesList = new ArrayList<>();
        NodesListItem nodesListItem = new NodesListItem();
        nodesListItem.setClusterId("id");
        nodesListItem.setClusterName("cluster");
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setNamespace("namespace1");
        commonMetaData.setName("name1");
        nodesListItem.setMetadata(commonMetaData);
        CommonStatus commonStatus = new CommonStatus();
        Map<String, Quantity> allocatable = new HashMap<>();
        allocatable.put("type", new Quantity(BigDecimal.valueOf(10000), Quantity.Format.DECIMAL_SI));
        allocatable.put("cpu", new Quantity(BigDecimal.valueOf(20000), Quantity.Format.DECIMAL_SI));
        allocatable.put("memory", new Quantity(BigDecimal.valueOf(30000), Quantity.Format.DECIMAL_SI));
        commonStatus.setAllocatable(allocatable);
        Map<String, Quantity> usage = new HashMap<>();
        usage.put("type", new Quantity(BigDecimal.valueOf(1000), Quantity.Format.DECIMAL_SI));
        usage.put("cpu", new Quantity(BigDecimal.valueOf(4000), Quantity.Format.DECIMAL_SI));
        usage.put("memory", new Quantity(BigDecimal.valueOf(5000), Quantity.Format.DECIMAL_SI));
        nodesListItem.setStatus(commonStatus);
        nodesListItem.setUsage(usage);

        NodesListItem nodesListItem2 = new NodesListItem();
        nodesListItem2.setClusterId("id2");
        nodesListItem2.setClusterName("cluster2");
        CommonMetaData commonMetaData2 = new CommonMetaData();
        commonMetaData2.setNamespace("namespace2");
        commonMetaData2.setName("name2");
        nodesListItem2.setMetadata(commonMetaData2);
        CommonStatus commonStatus2 = new CommonStatus();
        Map<String, Quantity> allocatable2 = new HashMap<>();
        allocatable2.put("type", new Quantity(BigDecimal.valueOf(5000), Quantity.Format.DECIMAL_SI));
        allocatable2.put("cpu", new Quantity(BigDecimal.valueOf(20000), Quantity.Format.DECIMAL_SI));
        allocatable2.put("memory", new Quantity(BigDecimal.valueOf(30000), Quantity.Format.DECIMAL_SI));
        commonStatus2.setAllocatable(allocatable2);
        Map<String, Quantity> usage2 = new HashMap<>();
        usage2.put("type", new Quantity(BigDecimal.valueOf(2000), Quantity.Format.DECIMAL_SI));
        usage2.put("cpu", new Quantity(BigDecimal.valueOf(4000), Quantity.Format.DECIMAL_SI));
        usage2.put("memory", new Quantity(BigDecimal.valueOf(5000), Quantity.Format.DECIMAL_SI));
        nodesListItem2.setStatus(commonStatus2);
        nodesListItem2.setUsage(usage2);
        nodesList.add(nodesListItem2);

        metricsService.topNodes(nodesList, "type", 2);

    }

    @Test
    public void generateNodeUsageMap() {
    }

    @Test
    public void findNodePercentage() {
    }

    @Test
    public void convertPercnUnit() {
    }

    @Test
    public void convertUsageUnit() {
    }
}