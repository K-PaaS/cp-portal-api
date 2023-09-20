package org.container.platform.api.metrics;

import org.container.platform.api.clusters.nodes.NodesList;
import org.container.platform.api.clusters.nodes.support.NodesListItem;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.metrics.custom.BaseExponent;
import org.container.platform.api.metrics.custom.ContainerMetrics;
import org.container.platform.api.metrics.custom.Quantity;
import org.container.platform.api.workloads.pods.Pods;
import org.container.platform.api.workloads.pods.PodsList;
import org.container.platform.api.workloads.pods.support.PodsListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.container.platform.api.overview.support.SuffixBase.*;


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


    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsService.class);

    /**
     * Instantiates a new Roles service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public MetricsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * Metrics Node 목록 조회(Get Metrics List for Nodes)
     *
     * @param params the params
     * @return the NodesMetricsList
     */
    public NodesMetricsList getNodesMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsNodesListUrl(), HttpMethod.GET, null, Map.class, params);
        NodesMetricsList nodeMetricsList = commonService.setResultObject(responseMap, NodesMetricsList.class);
        return (NodesMetricsList) commonService.setResultModel(nodeMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Metrics Pods 목록 조회(Get Metrics List for Pods)
     *
     * @param params the params
     * @return the PodsMetricsList
     */
    public PodsMetricsList getPodsMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsListUrl(), HttpMethod.GET, null, Map.class, params);
        PodsMetricsList podsMetricsList = commonService.setResultObject(responseMap, PodsMetricsList.class);
        return (PodsMetricsList) commonService.setResultModel(podsMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Nodes Metrics 조회(Get Metrics for Nodes)
     *
     * @param nodeName         the nodeName
     * @param nodesMetricsList the nodesMetricsList
     * @return the NodesMetricsItems
     */
    public NodesMetricsItems findNodeMetric(String nodeName, NodesMetricsList nodesMetricsList) {
        for (NodesMetricsItems metric : nodesMetricsList.getItems()) {
            if (metric.getName().equals(nodeName)) {
                return metric;
            }
        }
        return null;
    }


    /**
     * 클러스터 노드 평균 사용량 조회 (Get Nodes Average Usage)
     *
     * @param nodesList        the nodesList
     * @param nodesMetricsList the nodesMetricsList
     * @param type             the type
     * @return the double
     */
    public double findAllNodesAvgUsage(NodesList nodesList, NodesMetricsList nodesMetricsList, String type) {

        double sumCapacity = 0.0;
        double sumUsage = 0.0;

        for (NodesListItem node : nodesList.getItems()) {
            Quantity capacity = node.getStatus().getCapacity().get(type);
            Quantity usage = findNodeMetric(node.getName(), nodesMetricsList).getUsage().get(type);
            sumCapacity += capacity.getNumber().doubleValue();
            sumUsage += usage.getNumber().doubleValue();
        }

        return sumUsage / sumCapacity;
    }


    /**
     * Pods 내 ContainerMetrics 합계 (Sum Container Metrics in Pods)
     *
     * @param podsMetricsItems the podsMetricsItems
     * @param type             the type
     * @return the double
     */
    public static double podMetricSum(PodsMetricsItems podsMetricsItems, String type) {
        double sum = 0;
        for (ContainerMetrics containerMetrics : podsMetricsItems.getContainers()) {
            Quantity value = containerMetrics.getUsage().get(type);
            if (value != null) {
                sum += value.getNumber().doubleValue();
            }
        }
        return sum;
    }

    /**
     * Top Pods 계산 (get TopN Pods)
     *
     * @param podsMetricsList the podsMetricsList
     * @param type            the type
     * @param topN            the topN
     * @return the List<TopPods>
     */
    public List<TopPods> topPods(List<PodsMetricsItems> podsMetricsList, String type, Integer topN) {
        List<PodsMetricsItems> items = podsMetricsList;
        Collections.sort(
                items,
                new Comparator<PodsMetricsItems>() {
                    @Override
                    public int compare(PodsMetricsItems pm0, PodsMetricsItems pm1) {
                        double m0 =
                                podMetricSum(pm0, type);
                        double m1 =
                                podMetricSum(pm1, type);
                        return Double.compare(m0, m1) * -1; // sort high to low
                    }

                });

        if (items.size() > topN) {
            items = items.subList(0, topN);
        }

        // top pods 변환
        List<TopPods> topPods = items.stream().map(x -> new TopPods(x.getNamespace(), x.getName(),
                generatePodsUsageMap(Constants.CPU, x), generatePodsUsageMap(Constants.MEMORY, x))).collect(Collectors.toList());

        return topPods;
    }


    /**
     * Pods 사용량 Map 반환 (Return Map for Pods Usage)
     *
     * @param type the type
     * @param pm   the podsMetricsItems
     * @return the Map<String, String>
     */
    public Map<String, Object> generatePodsUsageMap(String type, PodsMetricsItems pm) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, podMetricSum(pm, type)));
        return result;
    }


    /**
     * Top Nodes 계산 (get TopN Nodes)
     *
     * @param nodesList the nodesList
     * @param type      the type
     * @param topN      the topN
     * @return the List<TopNodes>
     */
    public List<TopNodes> topNodes(List<NodesListItem> nodesList, String type, Integer topN) {
        List<NodesListItem> items = nodesList;
        Collections.sort(
                items,
                new Comparator<NodesListItem>() {
                    @Override
                    public int compare(NodesListItem nm0, NodesListItem nm1) {
                        double p0 = findNodePercentage(nm0, type);
                        double p1 = findNodePercentage(nm1, type);
                        return Double.compare(p0, p1) * -1; // sort high to low
                    }
                });


        if (items.size() > topN) {
            items = items.subList(0, topN);
        }

        // top nodes 변환
        List<TopNodes> topNodes = items.stream().map(x ->
                new TopNodes(x.getClusterName(), x.getClusterId(), x.getName(), generateNodeUsageMap(Constants.CPU, x), generateNodeUsageMap(Constants.MEMORY, x))
        ).collect(Collectors.toList());
        return topNodes;
    }


    /**
     * Nodes 사용량 Map 반환 (Return Map for Nodes Usage)
     *
     * @param type the type
     * @param node the nodesListItem
     * @return the Map<String, String>
     */
    public Map<String, Object> generateNodeUsageMap(String type, NodesListItem node) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, node.getUsage().get(type).getNumber().doubleValue()));
        result.put(Constants.PERCENT, convertPercnUnit(findNodePercentage(node, type)));
        return result;
    }


    /**
     * Nodes 사용량 Percent 계산 (Get Nodes Usage Percent)
     *
     * @param node the nodesListItem
     * @param type the type
     * @return the double
     */
    public double findNodePercentage(NodesListItem node, String type) {
        Quantity capacity = node.getStatus().getAllocatable().get(type);
        Quantity usage = node.getUsage().get(type);
        if (capacity == null) {
            return Double.POSITIVE_INFINITY;
        }
        return usage.getNumber().doubleValue() / capacity.getNumber().doubleValue();
    }


    /**
     * Percent String 포맷 (Percent String format)
     *
     * @param value the value
     * @return the String
     */
    public long convertPercnUnit(double value) {
        return Math.round(value * 100);
    }


    /**
     * 사용량 단위 변환 (Convert Usage Units)
     *
     * @param type  the type
     * @param usage the usage
     * @return the String
     */
    public long convertUsageUnit(String type, double usage) {
        BaseExponent baseExpont = null;
        String unit = "";
        if (type.equals(Constants.MEMORY)) {
            unit = Constants.MEMORY_UNIT;
            baseExpont = suffixToBinary.get(unit);
        } else {
            unit = Constants.CPU_UNIT;
            baseExpont = suffixToDecimal.get(unit);
        }
        double multiply = Math.pow(baseExpont.getBase(), -baseExpont.getExponent());
        return Math.round(usage * multiply);
    }


    /**
     * Pod Metrics 조회(Get Metrics for Pods)
     *
     * @param podName         the podName
     * @param podsMetricsList the podsMetricsList
     * @return the PodsMetricsItems
     */
    public PodsMetricsItems findPodsMetrics(String podName, PodsMetricsList podsMetricsList) {
        for (PodsMetricsItems metric : podsMetricsList.getItems()) {
            if (metric.getName().equals(podName)) {
                return metric;
            }
        }
        return null;
    }


    /**
     * Pod Metrics 조회(Get Metrics for Nodes)
     *
     * @param podsList the podsList
     * @param params   the params
     * @return the PodsList
     */
    public PodsList setPodsMetrics(PodsList podsList, Params params) {
        PodsMetricsList podsMetricsList;
        try {
            //get pod metrics data
            podsMetricsList = getPodsMetricsList(params);
        } catch (Exception e) {
            LOGGER.info(CommonUtils.loggerReplace("[EXCEPTION OCCURRED WHILE GET PODS METRICS LIST...] >> " + e.getMessage()));
            return podsList;
        }

        for (PodsListItem pods : podsList.getItems()) {
            PodsMetricsItems podsMetricsItems;
            try {
                podsMetricsItems = findPodsMetrics(pods.getName(), podsMetricsList);
                pods.setCpu(generatePodsUsageMapWithUnit(Constants.CPU, podsMetricsItems));
                pods.setMemory(generatePodsUsageMapWithUnit(Constants.MEMORY, podsMetricsItems));
            } catch (Exception e) {
            }

        }
        return podsList;
    }


    /**
     * Pods 사용량 Map 반환 (Return Map for Pods Usage)
     *
     * @param type the type
     * @param pm   the podsMetricsItems
     * @return the Map<String, String>
     */
    public Map<String, Object> generatePodsUsageMapWithUnit(String type, PodsMetricsItems pm) {
        String unit = (type.equals(Constants.CPU)) ? Constants.CPU_UNIT : Constants.MEMORY_UNIT;
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, podMetricSum(pm, type)) + unit);
        return result;
    }


    /**
     * Metrics Pods 상세 조회(Get Metrics for Pods)
     *
     * @param params the params
     * @return the PodsMetricsList
     */
    public PodsMetricsItems getPodsMetricsDetails(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsGetUrl(), HttpMethod.GET, null, Map.class, params);
        PodsMetricsItems podsMetricsItems = commonService.setResultObject(responseMap, PodsMetricsItems.class);
        return podsMetricsItems;
    }


    /**
     * Metrics Pods 상세 설정(Setting Metrics for Pods)
     *
     * @param params the params
     * @return the Pods
     */
    public Pods setPodsMetricsDetails(Pods pods, Params params) {
        PodsMetricsItems podsMetricsItems;
        try {
            //get pod metrics detail data
            podsMetricsItems = getPodsMetricsDetails(params);
            pods.setCpu(generatePodsUsageMapWithUnit(Constants.CPU, podsMetricsItems));
            pods.setMemory(generatePodsUsageMapWithUnit(Constants.MEMORY, podsMetricsItems));
        } catch (Exception e) {
            LOGGER.info(CommonUtils.loggerReplace("[EXCEPTION OCCURRED WHILE GET PODS METRICS DETAIL ...] >> " + e.getMessage()));
            return pods;
        }
        return pods;
    }


}