/*
package org.paasta.container.platform.api.overview;


import io.kubernetes.client.custom.NodeMetrics;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.apache.commons.lang3.tuple.Pair;
import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.VaultService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.overview.support.Count;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.kubernetes.client.extended.kubectl.Kubectl.top;
import static io.kubernetes.client.extended.kubectl.Kubectl.version;

@Service
public class GlobalOverviewTestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOverviewTestService.class);

    private final VaultService vaultService;
    private final CommonService commonService;
    private final UsersService usersService;


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

    private ApiClient client;
    private CoreV1Api api;
    private V1NodeList v1NodeList;

    public GlobalOverviewTestService(VaultService vaultService, CommonService commonService, UsersService usersService) {
        this.vaultService = vaultService;
        this.commonService = commonService;
        this.usersService = usersService;
    }


    public GlobalOverview getGlobalOverview(Params params) {
        // get cluster info

        // 관리자와 mapping 된 클러스터 목록 조회


        Clusters clusters = vaultService.getClusterDetails(params.getCluster());
        client = Config.fromToken(clusters.getClusterApiUrl(), clusters.getClusterToken(), false);
        api = new CoreV1Api(client);

        // node 정보 조회
        getNodes();


        GlobalOverview gOverview = new GlobalOverview(getKubectlVersion(), getNodesCount(), getPodCount(), getPvcCount(), getPvCount(), getNodeUsage());

        return (GlobalOverview) commonService.setResultModel(gOverview, Constants.RESULT_STATUS_SUCCESS);
    }


    public String getKubectlVersion() {
        try {
            return version().apiClient(client).execute().getGitVersion();
        } catch (Exception e) {
            LOGGER.info("Failed Get KubectlVersion : {}", CommonUtils.loggerReplace(e.getMessage()));
            return NULL_VAL;
        }

    }


    public Count getNodesCount() {

        int nodeReadyCount = 0;
        Map<String, Object> result = new HashMap<>();

        try {
            V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null,
                    null, null, null);

            for (V1Node node : v1NodeList.getItems()) {
                List<V1NodeCondition> nodeConditions = node.getStatus().getConditions().stream().filter(x -> x.getType().equalsIgnoreCase(NODE_CONDITIONS_READY)
                        && x.getStatus().equalsIgnoreCase(STATUS_TRUE)).collect(Collectors.toList());

                if (nodeConditions.size() > 0) {
                    nodeReadyCount++;
                }
            }

            return new Count(v1NodeList.getItems().size(), nodeReadyCount);
        } catch (Exception e) {
            return NULL_COUNT;
        }

    }


    public Count getPodCount() {

        try {

            V1PodList v1PodList = api.listPodForAllNamespaces(null, null, null, null,
                    null, null, null, null, null, null);

            List<V1Pod> runningPods = v1PodList.getItems().stream().filter(x -> x.getStatus().getPhase().equalsIgnoreCase(STATUS_RUNNING)).collect(Collectors.toList());
            return new Count(runningPods.size(), v1PodList.getItems().size());

        } catch (Exception e) {
            return NULL_COUNT;
        }

    }


    public Count getPvcCount() {
        try {
            V1PersistentVolumeClaimList v1PersistentVolumeClaimList = api.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null,
                    null, null, null, null, null);
            List<V1PersistentVolumeClaim> boundPVC = v1PersistentVolumeClaimList.getItems().stream().filter(x -> x.getStatus().getPhase().equalsIgnoreCase(STATUS_BOUND)).collect(Collectors.toList());
            return new Count(boundPVC.size(), v1PersistentVolumeClaimList.getItems().size());

        } catch (Exception e) {
            return NULL_COUNT;
        }

    }


    public Count getPvCount() {

        try {
            V1PersistentVolumeList v1PersistentVolumeList = api.listPersistentVolume(null, null, null, null, null, null,
                    null, null, null, null);
            List<V1PersistentVolume> boundPV = v1PersistentVolumeList.getItems().stream().filter(x -> x.getStatus().getPhase().equalsIgnoreCase(STATUS_BOUND)).collect(Collectors.toList());

            return new Count(boundPV.size(), v1PersistentVolumeList.getItems().size());

        } catch (Exception e) {
            return NULL_COUNT;
        }

    }

    public Map<String, Object> getNodeUsage() {

        Map<String, Object> usage = new HashMap<>();
        try {
            List<Pair<V1Node, NodeMetrics>> nodesMetrics = top(V1Node.class, NodeMetrics.class).apiClient(client).metric(CPU).execute();
            DecimalFormat df = new DecimalFormat("#%");
            usage.put(CPU, df.format(findNodePercentage(CPU, nodesMetrics)));
            usage.put(MEMORY, df.format(findNodePercentage(MEMORY, nodesMetrics)));
            return usage;

        } catch (Exception e) {
            usage.put(CPU, NULL_VAL);
            usage.put(MEMORY, NULL_VAL);
            return usage;
        }

    }

    public double findNodePercentage(String type, List<Pair<V1Node, NodeMetrics>> nodesMetrics) {

        double sumCapacity = 0;
        double sumUsage = 0;

        for (Pair<V1Node, NodeMetrics> node : nodesMetrics) {
            Quantity capacity = node.getKey().getStatus().getCapacity().get(type);
            Quantity usage = node.getValue().getUsage().get(type);
            sumCapacity += capacity.getNumber().doubleValue();
            sumUsage += usage.getNumber().doubleValue();
        }

        return sumUsage / sumCapacity;
    }


    /////////////////////////

    public String getKubeletVersion() {
        try {
            V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null,
                    null, null, null);
            List<V1Node> masterNode = v1NodeList.getItems().stream().filter(x -> x.getMetadata().getLabels().containsKey(LABEL_MASTER_NODE)).collect(Collectors.toList());

            if (masterNode.size() > 0) {
                return masterNode.get(0).getStatus().getNodeInfo().getKubeletVersion();
            }
            return NULL_VAL;
        } catch (Exception e) {
            return NULL_VAL;
        }

    }

    public void getNodes() {
        try {
            v1NodeList = api.listNode(null, null, null, null, null, null, null,
                    null, null, null);
        } catch (Exception e) {
        }
    }


    public void getMasterNode() {
        try {

        } catch (Exception e) {
        }
    }

}

*/
