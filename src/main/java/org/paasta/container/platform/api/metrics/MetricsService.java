package org.paasta.container.platform.api.metrics;

import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.support.NodesListItem;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.metrics.custom.BaseExponent;
import org.paasta.container.platform.api.metrics.custom.ContainerMetrics;
import org.paasta.container.platform.api.metrics.custom.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.overview.support.SuffixBase.*;


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
     * @return the NodesMetricsList
     */
    public NodesMetricsList getNodesMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsNodesListUrl(), HttpMethod.GET, null, Map.class, params);
        NodesMetricsList nodeMetricsList = commonService.setResultObject(responseMap, NodesMetricsList.class);
        return (NodesMetricsList) commonService.setResultModel(nodeMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Metrics Pods 목록 조회(Get Metrics for Pods)
     *
     * @param params the params
     * @return the roles list
     */
    public PodsMetricsList getPodsMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsListUrl(), HttpMethod.GET, null, Map.class, params);
        PodsMetricsList podsMetricsList = commonService.setResultObject(responseMap, PodsMetricsList.class);
        return (PodsMetricsList) commonService.setResultModel(podsMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }


    public NodesMetricsItems findNodeMetric(String nodeName, NodesMetricsList nodesMetricsList) {
        for (NodesMetricsItems metric : nodesMetricsList.getItems()) {
            if (metric.getName().equals(nodeName)) {
                return metric;
            }
        }
        return null;
    }


    public double findClusterUsagePercentage(NodesList nodesList, NodesMetricsList nodesMetricsList, String type) {

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

    public List<TopPods> topPods(List<PodsMetricsItems> podsMetricsList, String type) {
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

        items = items.subList(0, 10);
        List<TopPods> topPods = items.stream().map(x -> new TopPods(x.getClusterName(), x.getClusterId(), x.getNamespace(),
                x.getName(), convertUsageUnit(type, podMetricSum(x, type)))).collect(Collectors.toList());

        return topPods;
    }


    public List<TopNodes> topNodes(List<NodesListItem> nodesList, String type) {
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

        items = items.subList(0, 10);


        // top nodes 변환
        List<TopNodes> topNodes = items.stream().map(x -> new TopNodes(x.getClusterName(), x.getClusterId(),
                x.getName(), convertPercnUnit(findNodePercentage(x, type)),
                convertUsageUnit(type, x.getUsage().get(type).getNumber().doubleValue()))).collect(Collectors.toList());
        return topNodes;
    }


    public double findNodePercentage(NodesListItem node, String type) {
        Quantity capacity = node.getStatus().getAllocatable().get(type);
        Quantity usage = node.getUsage().get(type);
        if (capacity == null) {
            return Double.POSITIVE_INFINITY;
        }
        return usage.getNumber().doubleValue() / capacity.getNumber().doubleValue();
    }


    public String convertPercnUnit(double value) {
        return String.format("%.2f%%", (value) * 100);
    }


    public String convertUsageUnit(String type, double usage) {
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
        return Math.round(usage * multiply) + unit;
    }

}