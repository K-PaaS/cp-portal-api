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
import org.paasta.container.platform.api.overview.support.Status;
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

import java.util.*;
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
        List<Status> deploymentsUsage = getDeploymentsUsage(deploymentsList);

        // pods usage
        List<Status> podsUsage = getPodsUsage(podsList);

        // replicaSets usage
        List<Status> replicaSetsUsage = getReplicaSetsUsage(replicaSetsList);

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
    private List<Status> getDeploymentsUsage(DeploymentsList deploymentsList) {
        List<Status> statusList = new ArrayList<>();
        int failedCnt = 0;
        int runningCnt = 0;

        if(getCommonCnt(deploymentsList) < 1) {
            return statusList;
        }

        for (int i = 0; i < getCommonCnt(deploymentsList); i++) {
            DeploymentsStatus status = commonService.getField(STATUS_FIELD_NAME, deploymentsList.getItems().get(i));
            // status: unavailableReplicas, replicas, availableReplicas
            if (status.getUnavailableReplicas() > 0 && status.getReplicas() != 0 && status.getReplicas() != status.getAvailableReplicas()) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }


        Status running = new Status(STATUS_RUNNING, runningCnt, convertToPercent(runningCnt, getCommonCnt(deploymentsList)));
        Status failed = new Status(STATUS_FAILED, failedCnt, convertToPercent(failedCnt, getCommonCnt(deploymentsList)));

        statusList.add(running);
        statusList.add(failed);

        return statusList;

    }


    /**
     * Overview 조회를 위한 공통 Pods 사용량 조회(Get Common Deployment Usage for getting Overview according to namespaces)
     *
     * @param podsList the pods list
     * @return the map
     */
    private List<Status> getPodsUsage(PodsList podsList) {
        List<Status> statusList = new ArrayList<>();
        podsList.getItems().stream().map(x -> x.getPhase()).collect(Collectors.groupingBy(s -> s)).forEach((k, v) -> {
            Status status = new Status(k, v.size(), convertToPercent(v.size(), getCommonCnt(podsList)));
            statusList.add(status);
        });

        statusList.sort(Comparator.comparing(Status::getPercent).reversed());

        return statusList;
    }


    /**
     * Overview 조회를 위한 공통 ReplicaSets 사용량 조회(Get Common ReplicaSets Usage for getting Overview according to namespaces)
     *
     * @param replicaSetsList the replicaSets list
     * @return the map
     */
    private List<Status> getReplicaSetsUsage(ReplicaSetsList replicaSetsList) {
        List<Status> statusList = new ArrayList<>();
        int failedCnt = 0;
        int runningCnt = 0;

        if(getCommonCnt(replicaSetsList) < 1) {
            return statusList;
        }

        for (int i = 0; i < getCommonCnt(replicaSetsList); i++) {
            CommonStatus status = commonService.getField(STATUS_FIELD_NAME, replicaSetsList.getItems().get(i));
            // status -> AvailableReplicas
            if (status.getAvailableReplicas() < status.getReplicas() && status.getReplicas() > 0) {
                failedCnt++;
            } else {
                runningCnt++;
            }
        }

        Status running = new Status(STATUS_RUNNING, runningCnt, convertToPercent(runningCnt, getCommonCnt(replicaSetsList)));
        Status failed = new Status(STATUS_FAILED, failedCnt, convertToPercent(failedCnt, getCommonCnt(replicaSetsList)));

        statusList.add(running);
        statusList.add(failed);

        return statusList;
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
     * @param count the  count
     * @param total the  total
     * @return the long
     */
    private long convertToPercent(int count, int total) {
        double percent = ((double) count / (double) total) * 100;
        return Math.round(percent);

    }
}