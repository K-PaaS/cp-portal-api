package org.paasta.container.platform.api.overview;

import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.clusters.nodes.support.NodesListItem;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.VaultService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.metrics.MetricsItems;
import org.paasta.container.platform.api.metrics.MetricsList;
import org.paasta.container.platform.api.metrics.MetricsService;
import org.paasta.container.platform.api.overview.support.Count;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.PersistentVolumeClaimsList;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.PersistentVolumeClaimsService;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsListItem;
import org.paasta.container.platform.api.storages.persistentVolumes.PersistentVolumesList;
import org.paasta.container.platform.api.storages.persistentVolumes.PersistentVolumesService;
import org.paasta.container.platform.api.storages.persistentVolumes.support.PersistentVolumesListItem;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.pods.PodsList;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.paasta.container.platform.api.workloads.pods.support.PodsListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
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
    private final PodsService podsService;
    private final PersistentVolumesService persistentVolumesService;
    private final PersistentVolumeClaimsService persistentVolumeClaimsService;
    private final MetricsService metricsService;

    public static final String NULL_VAL = "-";
    public static final String STATUS_TRUE = "True";
    public static final String STATUS_RUNNING = "Running";
    public static final String NODE_CONDITIONS_READY = "Ready";
    public static final String STATUS_BOUND = "Bound";

    public static final String CPU = "cpu";
    public static final String MEMORY = "memory";

    public static final String ALL_VAL = "all";
    public static final String COUNT_VAL = "count";

    public static final String LABEL_MASTER_NODE = "node-role.kubernetes.io/control-plane";

    public static final Count NULL_COUNT = new Count(NULL_VAL, NULL_VAL);


    public GlobalOverviewService(VaultService vaultService, CommonService commonService, UsersService usersService,
                                 NodesService nodesService, PodsService podsService, PersistentVolumesService persistentVolumesService,
                                 PersistentVolumeClaimsService persistentVolumeClaimsService,  MetricsService metricsService) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.usersService = usersService;
        this.nodesService = nodesService;
        this.podsService = podsService;
        this.persistentVolumesService = persistentVolumesService;
        this.persistentVolumeClaimsService = persistentVolumeClaimsService;
        this.metricsService = metricsService;

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
     *
     * @param params the params
     * @return count the count
     */
    public Count getPodCount(Params params) {
        PodsList podsList = podsService.getPodsList(params);
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
    public Count getPvcCount(Params params){
        PersistentVolumeClaimsList persistentVolumeClaimsList = persistentVolumeClaimsService.getPersistentVolumeClaimsList(params);
        List<PersistentVolumeClaimsListItem> boundPVC = persistentVolumeClaimsList.getItems().stream().filter(x->x.getPersistentVolumeClaimStatus().equalsIgnoreCase(STATUS_BOUND)).collect(Collectors.toList());
        return new Count(boundPVC.size(), persistentVolumeClaimsList.getItems().size());
    }




    public void getClusterNodeUsage(Params params){

        NodesList nodesList = nodesService.getNodesList(params);
        MetricsList metricsList = metricsService.getMetricsNodesList(params);


        for(NodesListItem node : nodesList.getItems()) {
            MetricsItems metrics = metricsService.findNodeMetric(node.getName(), metricsList);
            System.out.println(node.getName());
            System.out.println(metrics.getUsage().toString());
            System.out.println(node.getStatus().getCapacity().toString());

        }

    }


}