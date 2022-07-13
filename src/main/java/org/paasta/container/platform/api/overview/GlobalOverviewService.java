package org.paasta.container.platform.api.overview;

import org.paasta.container.platform.api.clusters.namespaces.NamespacesList;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesService;
import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.clusters.nodes.support.NodesListItem;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.VaultService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.metrics.*;
import org.paasta.container.platform.api.overview.support.Count;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.PersistentVolumeClaimsList;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.PersistentVolumeClaimsService;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsListItem;
import org.paasta.container.platform.api.storages.persistentVolumes.PersistentVolumesList;
import org.paasta.container.platform.api.storages.persistentVolumes.PersistentVolumesService;
import org.paasta.container.platform.api.storages.persistentVolumes.support.PersistentVolumesListItem;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.pods.PodsList;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.paasta.container.platform.api.workloads.pods.support.PodsListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalOverviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOverviewService.class);

    private final VaultService vaultService;
    private final CommonService commonService;
    private final UsersService usersService;
    private final NodesService nodesService;
    private final NamespacesService namespacesService;
    private final PodsService podsService;
    private final PersistentVolumesService persistentVolumesService;
    private final PersistentVolumeClaimsService persistentVolumeClaimsService;
    private final MetricsService metricsService;


    public static final String NULL_VAL = "-";
    public static final String STATUS_TRUE = "True";
    public static final String STATUS_RUNNING = "Running";
    public static final String NODE_CONDITIONS_READY = "Ready";
    public static final String STATUS_BOUND = "Bound";


    public static final String CPU_UNIT = "m";
    public static final String MEMORY_UNIT = "Mi";

    public static final String ALL_VAL = "all";
    public static final String COUNT_VAL = "count";

    public static final String LABEL_MASTER_NODE = "node-role.kubernetes.io/control-plane";

    public static final Count NULL_COUNT = new Count(0, 0);


    public GlobalOverviewService(VaultService vaultService, CommonService commonService, UsersService usersService,
                                 NodesService nodesService, NamespacesService namespacesService,
                                 PodsService podsService, PersistentVolumesService persistentVolumesService,
                                 PersistentVolumeClaimsService persistentVolumeClaimsService, MetricsService metricsService) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.usersService = usersService;
        this.nodesService = nodesService;
        this.namespacesService = namespacesService;
        this.podsService = podsService;
        this.persistentVolumesService = persistentVolumesService;
        this.persistentVolumeClaimsService = persistentVolumeClaimsService;
        this.metricsService = metricsService;

    }


    public GlobalOverview getGlobalOverview(Params params) {
        List<PodsMetricsItems> podsMetricsAllCluster = new ArrayList<>();
        List<NodesListItem> nodesListAllCluster = new ArrayList<>();
        params.setIsGlobal(true);
        params.setNamespace(Constants.ALL_NAMESPACES);

        UsersList mappingClusters = usersService.getMappingClustersListByUser(params);
        Integer clusterCnt = 0;
        Integer namespaceCnt = 0;
        Integer pvcCnt = 0;
        Integer pvCnt = 0;
        Integer podCnt = 0;

        List<GlobalOverviewItems> items = new ArrayList<>();
        for (Users users : mappingClusters.getItems()) {
            // set cluster id
            params.setCluster(users.getClusterId());
            params.setClusterName(users.getClusterName());

            // get node data
            NodesList nodesList = nodesService.getNodesList(params);
            // get node metrics data
            NodesMetricsList nodesMetricsList = metricsService.getNodesMetricsList(params);
            setNodesMetrics(params, nodesList, nodesMetricsList);
            nodesListAllCluster.addAll(nodesList.getItems());

            // pod data
            PodsList podsList = podsService.getPodsList(params);
            // pod metrics data
            PodsMetricsList podsMetricsList = metricsService.getPodsMetricsList(params);
            setPodsMetrics(params, podsMetricsList);
            podsMetricsAllCluster.addAll(podsMetricsList.getItems());

            GlobalOverviewItems goi = new GlobalOverviewItems(users.getClusterId(), users.getClusterName(), users.getClusterProviderType(),
                    getKubeletVersion(nodesList), getNodeCount(nodesList), getPodCount(podsList),
                    getPvcCount(params), getPvCount(params), getClusterUsage(nodesList, nodesMetricsList));

            // namespace data
            NamespacesList namespacesList = namespacesService.getNamespacesList(params);

            // all count
            clusterCnt++;
            namespaceCnt += namespacesList.getItems().size();
            pvcCnt += goi.getPvcCount().getAll();
            pvCnt += goi.getPvCount().getAll();
            podCnt += goi.getPodCount().getAll();

            items.add(goi);
        }


        GlobalOverview globalOverview = new GlobalOverview(clusterCnt, namespaceCnt, pvcCnt, pvCnt, podCnt, items,
                metricsService.topNodes(nodesListAllCluster, Constants.CPU), metricsService.topNodes(nodesListAllCluster, Constants.MEMORY),
                metricsService.topPods(podsMetricsAllCluster, Constants.CPU), metricsService.topPods(podsMetricsAllCluster, Constants.MEMORY));


        return (GlobalOverview) commonService.setResultModel(globalOverview, Constants.RESULT_STATUS_SUCCESS);

    }

    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Master Node Kubelet 버전 조회
     *
     * @param nodesList the nodesLIst
     * @return the String
     */
    public String getKubeletVersion(NodesList nodesList) {
        List<NodesListItem> masterNode = nodesList.getItems().stream().filter(x -> x.getMetadata().getLabels().containsKey(LABEL_MASTER_NODE)).collect(Collectors.toList());
        if (masterNode.size() > 0) {
            return masterNode.get(0).getStatus().getNodeInfo().getKubeletVersion();
        }
        return NULL_VAL;
    }

    /**
     * Node Ready Status 'Ready' Count 조회
     *
     * @param nodesList the nodesLIst
     * @return count the count
     */
    public Count getNodeCount(NodesList nodesList) {
        List<NodesListItem> readyNodes = nodesList.getItems().stream().filter(x -> x.getReady().equalsIgnoreCase(STATUS_TRUE)).collect(Collectors.toList());
        return new Count(readyNodes.size(), nodesList.getItems().size());
    }

    /**
     * Pod Status 'Running' Count 조회
     * <p>
     * podsList the podsList
     *
     * @return count the count
     */
    public Count getPodCount(PodsList podsList) {
        List<PodsListItem> runningPods = podsList.getItems().stream().filter(x -> x.getPodStatus().equalsIgnoreCase(STATUS_RUNNING)).collect(Collectors.toList());
        return new Count(runningPods.size(), podsList.getItems().size());
    }

    /**
     * PV Status 'Bound' Count 조회
     *
     * @param params the params
     * @return count the count
     */
    public Count getPvCount(Params params) {
        PersistentVolumesList persistentVolumesList = persistentVolumesService.getPersistentVolumesList(params);
        List<PersistentVolumesListItem> boundPV = persistentVolumesList.getItems().stream().filter(x -> x.getPersistentVolumeStatus().equalsIgnoreCase(STATUS_BOUND)).collect(Collectors.toList());
        return new Count(boundPV.size(), persistentVolumesList.getItems().size());
    }

    /**
     * PVC Status 'Bound' Count 조회
     *
     * @param params the params
     * @return count the count
     */
    public Count getPvcCount(Params params) {
        PersistentVolumeClaimsList persistentVolumeClaimsList = persistentVolumeClaimsService.getPersistentVolumeClaimsList(params);
        List<PersistentVolumeClaimsListItem> boundPVC = persistentVolumeClaimsList.getItems().stream().filter(x -> x.getPersistentVolumeClaimStatus().equalsIgnoreCase(STATUS_BOUND)).collect(Collectors.toList());
        return new Count(boundPVC.size(), persistentVolumeClaimsList.getItems().size());
    }


    /**
     * 클러스터 cpu/memory 사용 % 계산
     *
     * @param nodesList        the nodesList
     * @param nodesMetricsList the nodesMetricsList
     * @return Map the map
     */
    public Map<String, Object> getClusterUsage(NodesList nodesList, NodesMetricsList nodesMetricsList) {

        DecimalFormat df = new DecimalFormat("#%");
        Map<String, Object> usage = new HashMap<>();
        usage.put(Constants.CPU, df.format(metricsService.findClusterUsagePercentage(nodesList, nodesMetricsList, Constants.CPU)));
        usage.put(Constants.MEMORY, df.format(metricsService.findClusterUsagePercentage(nodesList, nodesMetricsList, Constants.MEMORY)));

        return usage;
    }

    /**
     * Nodes 목록 클러스터 및 Metrics 정보 설정
     *
     * @param params           the params
     * @param nodesList        the nodesList
     * @param nodesMetricsList the nodesMetricsList
     * @return
     */
    public void setNodesMetrics(Params params, NodesList nodesList, NodesMetricsList nodesMetricsList) {
        for (NodesListItem node : nodesList.getItems()) {
            node.setClusterId(params.getCluster());
            node.setClusterName(params.getClusterName());
            node.setUsage(metricsService.findNodeMetric(node.getName(), nodesMetricsList).getUsage());
        }
    }


    /**
     * Pods 목록 클러스터 정보 설정
     *
     * @param params          the params
     * @param podsMetricsList the podsMetricsList
     * @return
     */
    public void setPodsMetrics(Params params, PodsMetricsList podsMetricsList) {
        for (PodsMetricsItems pm : podsMetricsList.getItems()) {
            pm.setClusterId(params.getCluster());
            pm.setClusterName(params.getClusterName());
        }
    }
}

