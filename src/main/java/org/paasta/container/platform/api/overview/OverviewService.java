package org.paasta.container.platform.api.overview;

import org.paasta.container.platform.api.clusters.namespaces.NamespacesListAdmin;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesService;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsListAdmin;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsService;
import org.paasta.container.platform.api.workloads.deployments.support.DeploymentsStatus;
import org.paasta.container.platform.api.workloads.pods.PodsListAdmin;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.paasta.container.platform.api.workloads.pods.support.ContainerStatusesItem;
import org.paasta.container.platform.api.workloads.pods.support.PodsStatus;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsListAdmin;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Overview Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.30
 **/
@Service
public class OverviewService {
    private static final String ORDER_BY_DEFAULT = "creationTime";
    private static final String ORDER_DEFAULT = "desc";
    private static final String STATUS_FIELD_NAME = "status";

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
     * @param namespacesService  the namespaces service
     * @param deploymentsService the deployments service
     * @param podsService        the pods service
     * @param replicaSetsService the replicaSets service
     * @param usersService       the users service
     * @param commonService      the common service
     * @param propertyService    the property service
     */
    @Autowired
    public OverviewService(NamespacesService namespacesService, DeploymentsService deploymentsService,
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
     * @param cluster the cluster
     * @return the overview
     */
    public Overview getOverviewAll(String cluster) {
        Overview overview = new Overview();

        // namespaces count
        NamespacesListAdmin namespacesListAll = (NamespacesListAdmin) namespacesService.getNamespacesListAdmin(0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");

        // deployments count
        DeploymentsListAdmin deploymentsListAll = getDeploymentsList(null);

        // pods count
        PodsListAdmin podsListAll = getPodsList(null);

        // replicaSets count
        ReplicaSetsListAdmin replicaSetsListAll = getReplicaSetsList(null);

        // users count
        int usersCnt =  getUsersListByNamespaceByOverview(cluster, propertyService.getDefaultNamespace());

        // deployments usage
        Map<String, Object> deploymentsUsage = getDeploymentsUsage(deploymentsListAll);

        // pods usage
        Map<String, Object> podsUsage = getPodsUsage(podsListAll);

        // replicaSets usage
        Map<String, Object> replicaSetsUsage = getReplicaSetsUsage(replicaSetsListAll);

        overview.setNamespacesCount(getCommonCnt(namespacesListAll));
        overview.setDeploymentsCount(getCommonCnt(deploymentsListAll));
        overview.setPodsCount(getCommonCnt(podsListAll));
        overview.setUsersCount(usersCnt);
        overview.setDeploymentsUsage(deploymentsUsage);
        overview.setPodsUsage(podsUsage);
        overview.setReplicaSetsUsage(replicaSetsUsage);

        return (Overview) commonService.setResultModelWithNextUrl(overview, Constants.RESULT_STATUS_SUCCESS, "EMPTY");
    }


    /**
     * Overview 조회(Get Overview)
     *
     * @param cluster the cluster
     * @param namespace the namespace
     * @return the overview
     */
    public Overview getOverview(String cluster, String namespace) {
        Overview overview = new Overview();

        // deployments count
        DeploymentsListAdmin deploymentsList = getDeploymentsList(namespace);

        // pods count
        PodsListAdmin podsList = getPodsList(namespace);

        // replicaSets count
        ReplicaSetsListAdmin replicaSetsList = getReplicaSetsList(namespace);

        // users count
        int usersCnt = getUsersListByNamespaceByOverview(cluster, namespace);

        // deployments usage
        Map<String, Object> deploymentsUsage = getDeploymentsUsage(deploymentsList);

        // pods usage
        Map<String, Object> podsUsage = getPodsUsage(podsList);

        // replicaSets usage
        Map<String, Object> replicaSetsUsage = getReplicaSetsUsage(replicaSetsList);

        overview.setNamespacesCount(1);
        overview.setDeploymentsCount(getCommonCnt(deploymentsList));
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
        return itemMetadata == null? 0 : itemMetadata.getAllItemCount();
    }


    /**
     * Overview 조회를 위한 Namespace 별 Deployments 목록 조회(Get Deployments list for getting overview)
     *
     * @param namespace the namespace
     * @return the deployments list admin
     */
    private DeploymentsListAdmin getDeploymentsList(String namespace){
        if(StringUtils.isEmpty(namespace)) {
            return (DeploymentsListAdmin) deploymentsService.getDeploymentsListAllNamespacesAdmin(0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
        }

        return (DeploymentsListAdmin) deploymentsService.getDeploymentsListAdmin(namespace,0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
    }


    /**
     * Overview 조회를 위한 Namespace 별 Pods 목록 조회(Get Pods list for getting overview)
     *
     * @param namespace the namespace
     * @return the pods list admin
     */
    private PodsListAdmin getPodsList(String namespace){
        if(StringUtils.isEmpty(namespace)) {
            return (PodsListAdmin) podsService.getPodsListAllNamespacesAdmin(0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
        }

        return (PodsListAdmin) podsService.getPodsListAdmin(namespace, 0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
    }


    /**
     * Overview 조회를 위한 Namespace 별 ReplicaSets 목록 조회(Get ReplicaSets list for getting overview)
     *
     * @param namespace the namespace
     * @return the replicaSets list admin
     */
    private ReplicaSetsListAdmin getReplicaSetsList(String namespace){
        if(StringUtils.isEmpty(namespace)) {
            return (ReplicaSetsListAdmin) replicaSetsService.getReplicaSetsListAllNamespacesAdmin(0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
        }

        return (ReplicaSetsListAdmin) replicaSetsService.getReplicaSetsListAdmin(namespace, 0,0, ORDER_BY_DEFAULT, ORDER_DEFAULT, "");
    }


    /**
     * All Overview, Overview 조회를 위한 공통 Deployments 사용량 조회(Get Common Deployment Usage for getting Overview according to namespaces)
     *
     * @param deploymentsList the deployments list
     * @return the map
     */
    private Map<String, Object> getDeploymentsUsage(DeploymentsListAdmin deploymentsList) {
        int failedCnt = 0;
        int runningCnt = 0;

        for(int i = 0; i < getCommonCnt(deploymentsList); i++) {
            DeploymentsStatus status = commonService.getField(STATUS_FIELD_NAME, deploymentsList.getItems().get(i));

            // status: unavailableReplicas, replicas, availableReplicas
            if(status.getUnavailableReplicas() > 0 && status.getReplicas() != 0 && status.getReplicas() != status.getAvailableReplicas()) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }
        return convertToPercentMap(runningCnt, failedCnt, getCommonCnt(deploymentsList));
    }


    /**
     * All Overview, Overview 조회를 위한 공통 Pods 사용량 조회(Get Common Deployment Usage for getting Overview according to namespaces)
     *
     * @param podsList the pods list
     * @return the map
     */
    private Map<String, Object> getPodsUsage(PodsListAdmin podsList) {
        int failedCnt = 0;
        int runningCnt = 0;

        for(int i = 0; i < getCommonCnt(podsList); i++) {
            PodsStatus status = commonService.getField(STATUS_FIELD_NAME, podsList.getItems().get(i));
            if(status.getContainerStatuses() != null) {
                ContainerStatusesItem item = status.getContainerStatuses().get(0);
                // containerStatuses -> state: waiting
                if(item.getState() != null) {
                    boolean result = item.getState().containsKey("waiting");
                    if(result) {
                        failedCnt++;
                    } else {
                        runningCnt++;
                    }
                } else {
                    failedCnt++;
                }

            } else {
                failedCnt++;
            }
        }

        return convertToPercentMap(runningCnt, failedCnt, getCommonCnt(podsList));
    }


    /**
     * All Overview, Overview 조회를 위한 공통 ReplicaSets 사용량 조회(Get Common ReplicaSets Usage for getting Overview according to namespaces)
     *
     * @param replicaSetsList the replicaSets list
     * @return the map
     */
    private Map<String, Object> getReplicaSetsUsage(ReplicaSetsListAdmin replicaSetsList) {
        int failedCnt = 0;
        int runningCnt = 0;

        for(int i = 0; i < getCommonCnt(replicaSetsList); i++) {
            CommonStatus status = commonService.getField(STATUS_FIELD_NAME, replicaSetsList.getItems().get(i));
            // status -> AvailableReplicas
            if(status.getAvailableReplicas() < status.getReplicas() && status.getReplicas() > 0) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }

        return convertToPercentMap(runningCnt, failedCnt, getCommonCnt(replicaSetsList));
    }


    /**
     * 사용량 계산 후 퍼센트로 변환(Convert to percentage after calculating usage)
     *
     * @param runningCnt the running count
     * @param failedCnt the failed count
     * @param totalCnt the total count
     * @return the map
     */
    private Map<String, Object> convertToPercentMap(int runningCnt, int failedCnt, int totalCnt) {
        Map<String, Object> map = new HashMap<>();

        String percentPattern = "0"; // 소수점 표현 시 "0.#, 0.##"
        DecimalFormat format = new DecimalFormat(percentPattern);

        double runningPercent = ((double)runningCnt/(double)totalCnt) * 100;
        double failedPercent = ((double)failedCnt/(double)totalCnt) * 100;

        String formedRunningPercent = Double.isNaN(runningPercent)? "0" :format.format(runningPercent);
        String formedFailedPercent = Double.isNaN(failedPercent)? "0" :format.format(failedPercent);

        map.put("running", formedRunningPercent);
        map.put("failed", formedFailedPercent);

        return map;
    }

    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users list
     */
    public Integer getUsersListByNamespaceByOverview(String cluster, String namespace) {
        UsersList usersList =  restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE
                .replace("{cluster:.+}", cluster)
                .replace("{namespace:.+}", namespace), HttpMethod.GET, null, UsersList.class);

        List<String> overviewUserList = usersList.getItems().stream().map(Users::getUserId).collect(Collectors.toList());
        overviewUserList = overviewUserList.stream().distinct().collect(Collectors.toList());

        return overviewUserList.size();
    }

}
