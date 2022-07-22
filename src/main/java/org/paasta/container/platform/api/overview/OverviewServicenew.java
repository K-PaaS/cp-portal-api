package org.paasta.container.platform.api.overview;

import org.paasta.container.platform.api.clusters.namespaces.NamespacesList;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesService;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonStatus;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsList;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsService;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsStatus;
import org.paasta.container.platform.api.workloads.pods.PodsList;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsList;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Overview Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 **/
@Service
public class OverviewServicenew {
    private static final String ORDER_BY_DEFAULT = "creationTime";
    private static final String ORDER_DEFAULT = "desc";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String STATUS_FAILED = "Failed";
    private static final String STATUS_RUNNING = "Running";

    private final NamespacesService namespacesService;
    private final DeploymentsService deploymentsService;
    private final PodsService podsService;
    private final ReplicaSetsService replicaSetsService;
    private final UsersService usersService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final RestTemplateService restTemplateService;

    /**
     * Instantiates a new Overview service
     *
     * @param namespacesService  the namespaces service
     * @param deploymentsService the deployments service
     * @param podsService        the pods service
     * @param replicaSetsService the replicaSets service
     * @param usersService       the users service
     * @param commonService      the common service
     * @param propertyService    the property service
     */
    @Autowired
    public OverviewServicenew(NamespacesService namespacesService, DeploymentsService deploymentsService,
                              PodsService podsService, ReplicaSetsService replicaSetsService, UsersService usersService,
                              CommonService commonService, PropertyService propertyService, RestTemplateService restTemplateService) {
        this.namespacesService = namespacesService;
        this.deploymentsService = deploymentsService;
        this.podsService = podsService;
        this.replicaSetsService = replicaSetsService;
        this.usersService = usersService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.restTemplateService = restTemplateService;
    }


    /**
     * 전체 Namespaces 의 Overview 조회(Get Overview in All Namespaces)
     *
     * @param params the params
     * @return the overview
     */
    public Overview getOverviewAll(Params params) {
        Overview overview = new Overview();
        NamespacesList namespacesList = new NamespacesList();

        // get deployments count
        DeploymentsList deploymentsList = getDeploymentsList(params);
        // get pods count
        PodsList podsList = getPodsList(params);
        // get replicaSets count
        ReplicaSetsList replicaSetsList = getReplicaSetsList(params);


        // if all namespace
        if (params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
            // get namespaces count
            namespacesList = namespacesService.getNamespacesList(params);
            params.setNamespace(propertyService.getDefaultNamespace());
        }

        // get users count
        int usersCnt = getUsersListByNamespaceByOverview(params);

        // deployments usage
        Map<String, Object> deploymentsUsage = getDeploymentsUsage(deploymentsList);

        // pods usage
        Map<String, Object> podsUsage = getPodsUsage(podsList);

        // replicaSets usage
        Map<String, Object> replicaSetsUsage = getReplicaSetsUsage(replicaSetsList);

        overview.setNamespacesCount(getCommonCnt(namespacesList));
        overview.setDeploymentsCount(getCommonCnt(deploymentsList));
        overview.setReplicaSetsCount(getCommonCnt(replicaSetsList));
        overview.setPodsCount(getCommonCnt(podsList));
        overview.setUsersCount(usersCnt);
        overview.setDeploymentsUsage(deploymentsUsage);
        overview.setPodsUsage(podsUsage);
        overview.setReplicaSetsUsage(replicaSetsUsage);

        return (Overview) commonService.setResultModel(overview, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 해당 Resource 총 개수 조회(Get resource's total size)
     *
     * @param resourceList the resource list
     * @return the int
     */
    private int getCommonCnt(Object resourceList) {
        CommonItemMetaData itemMetadata = commonService.getField("itemMetaData", resourceList);
        return itemMetadata == null ? 0 : itemMetadata.getAllItemCount();
    }


    /**
     * Overview 조회를 위한 Namespace 별 Deployments 목록 조회(Get Deployments list for getting overview)
     *
     * @param params the params
     * @return the deployments list
     */
    private DeploymentsList getDeploymentsList(Params params) {
        params.setSelectorType(Constants.RESOURCE_NAMESPACE);
        return deploymentsService.getDeploymentsList(params);
    }


    /**
     * Overview 조회를 위한 Namespace 별 Pods 목록 조회(Get Pods list for getting overview)
     *
     * @param params the params
     * @return the pods list admin
     */
    private PodsList getPodsList(Params params) {
        params.setSelectorType(Constants.RESOURCE_NAMESPACE);
        return podsService.getPodsList(params);
    }


    /**
     * Overview 조회를 위한 Namespace 별 ReplicaSets 목록 조회(Get ReplicaSets list for getting overview)
     *
     * @param params the params
     * @return the replicaSets list admin
     */
    private ReplicaSetsList getReplicaSetsList(Params params) {
        params.setSelectorType(Constants.RESOURCE_NAMESPACE);
        return replicaSetsService.getReplicaSetsList(params);
    }


    /**
     * Overview 조회를 위한 공통 Deployments 사용량 조회(Get Common Deployment Usage for getting Overview according to namespaces)
     *
     * @param deploymentsList the deployments list
     * @return the map
     */
    private Map<String, Object> getDeploymentsUsage(DeploymentsList deploymentsList) {
        HashMap<String, Integer> resultMap = new HashMap<>();
        int failedCnt = 0;
        int runningCnt = 0;

        for (int i = 0; i < getCommonCnt(deploymentsList); i++) {
            DeploymentsStatus status = commonService.getField(STATUS_FIELD_NAME, deploymentsList.getItems().get(i));
            // status: unavailableReplicas, replicas, availableReplicas
            if (status.getUnavailableReplicas() > 0 && status.getReplicas() != 0 && status.getReplicas() != status.getAvailableReplicas()) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }

        resultMap.put(STATUS_FAILED, failedCnt);
        resultMap.put(STATUS_RUNNING, runningCnt);

        return convertToPercentMap(resultMap, getCommonCnt(deploymentsList));

    }


    /**
     * Overview 조회를 위한 공통 Pods 사용량 조회(Get Common Deployment Usage for getting Overview according to namespaces)
     *
     * @param podsList the pods list
     * @return the map
     */
    private Map<String, Object> getPodsUsage(PodsList podsList) {
        HashMap<String, Integer> resultMap = new HashMap<>();
        podsList.getItems().stream().map(x -> x.getPhase()).collect(Collectors.groupingBy(s -> s)).forEach((k, v) -> resultMap.put(k, v.size()));
        return convertToPercentMap(resultMap, getCommonCnt(podsList));
    }


    /**
     * Overview 조회를 위한 공통 ReplicaSets 사용량 조회(Get Common ReplicaSets Usage for getting Overview according to namespaces)
     *
     * @param replicaSetsList the replicaSets list
     * @return the map
     */
    private Map<String, Object> getReplicaSetsUsage(ReplicaSetsList replicaSetsList) {
        HashMap<String, Integer> resultMap = new HashMap<>();
        int failedCnt = 0;
        int runningCnt = 0;

        for (int i = 0; i < getCommonCnt(replicaSetsList); i++) {
            CommonStatus status = commonService.getField(STATUS_FIELD_NAME, replicaSetsList.getItems().get(i));
            // status -> AvailableReplicas
            if (status.getAvailableReplicas() < status.getReplicas() && status.getReplicas() > 0) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }

        resultMap.put(STATUS_FAILED, failedCnt);
        resultMap.put(STATUS_RUNNING, runningCnt);

        return convertToPercentMap(resultMap, getCommonCnt(replicaSetsList));
    }


    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param params the params
     * @return the users list
     */
    public Integer getUsersListByNamespaceByOverview(Params params) {
        UsersList usersList = restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", params.getCluster())
                .replace("{namespace:.+}", params.getNamespace()), HttpMethod.GET, null, UsersList.class, params);

        List<String> overviewUserList = usersList.getItems().stream().map(Users::getUserId).collect(Collectors.toList());
        overviewUserList = overviewUserList.stream().distinct().collect(Collectors.toList());
        return overviewUserList.size();
    }


    /**
     * 사용량 계산 후 퍼센트로 변환(Convert to percentage after calculating usage)
     *
     * @param totalCnt the total count
     * @return the map
     */
    private Map<String, Object> convertToPercentMap(Map<String, Integer> items, int totalCnt) {
        Map<String, Object> result = new HashMap<>();

        String percentPattern = "0"; // 소수점 표현 시 "0.#, 0.##"
        DecimalFormat format = new DecimalFormat(percentPattern);

        for (String key : items.keySet()) {
            double percent = ((double) items.get(key) / (double) totalCnt) * 100;
            String formatPercent = Double.isNaN(percent) ? "0" : format.format(percent);
            result.put(key, formatPercent);
        }

        return result;

    }

}