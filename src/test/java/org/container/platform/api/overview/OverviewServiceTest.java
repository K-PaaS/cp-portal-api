package org.container.platform.api.overview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.clusters.namespaces.NamespacesList;
import org.container.platform.api.clusters.namespaces.NamespacesService;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.metrics.MetricsService;
import org.container.platform.api.metrics.PodsMetricsList;
import org.container.platform.api.overview.support.Status;
import org.container.platform.api.users.UsersList;
import org.container.platform.api.users.UsersModel;
import org.container.platform.api.users.UsersService;
import org.container.platform.api.workloads.deployments.DeploymentsList;
import org.container.platform.api.workloads.deployments.DeploymentsService;
import org.container.platform.api.workloads.pods.PodsList;
import org.container.platform.api.workloads.pods.PodsService;
import org.container.platform.api.workloads.pods.support.PodsListItem;
import org.container.platform.api.workloads.replicaSets.ReplicaSetsList;
import org.container.platform.api.workloads.replicaSets.ReplicaSetsService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class OverviewServiceTest {
    private static final String CLUSTER = "test-cluster";
    private static final String NAMESPACE = "test-namespace";
    private static final String EMPTY = "";

    private static Overview gFinalOverviewAllResultModel = null;
    private static Overview gFinalOverviewResultModel = null;

    private static Overview gFinalOverviewAllAdminResultModel = null;
    private static Overview gFinalOverviewAdminResultModel = null;

    private static DeploymentsList gResultDeploymentsListModel = null;

    private static PodsList gResultPodsListModel = null;

    private static ReplicaSetsList gResultReplicaSetsListModel = null;

    private static NamespacesList gResultNamespacesListModel = null;

    private static UsersList gResultUsersListModel = null;

    private static Params gParams = null;

    private static PodsMetricsList gResultPodsMetricsListModel = null;

    @Mock
    NamespacesService namespacesService;

    @Mock
    DeploymentsService deploymentsService;

    @Mock
    PodsService podsService;

    @Mock
    ReplicaSetsService replicaSetsService;

    @Mock
    UsersService usersService;

    @Mock
    MetricsService metricsService;

    @Mock
    PropertyService propertyService;

    @Mock
    CommonService commonService;

    @InjectMocks
    OverviewService overviewService;

    @Mock
    RestTemplateService restTemplateService;

    @Before
    public void setUp() throws Exception {
        CommonItemMetaData metadata = new CommonItemMetaData();
        metadata.setAllItemCount(0);
        metadata.setRemainingItemCount(0);

        gResultDeploymentsListModel = new DeploymentsList();
        gResultDeploymentsListModel.setItemMetaData(metadata);

        gResultPodsListModel = new PodsList();
        gResultPodsListModel.setItemMetaData(metadata);
        List<PodsListItem> podsListItems = new ArrayList<>();
        PodsListItem podsListItem = new PodsListItem();
        podsListItem.setContainerStatus("test");
        gResultPodsListModel.setItems(podsListItems);

        gResultReplicaSetsListModel = new ReplicaSetsList();
        gResultReplicaSetsListModel.setItemMetaData(metadata);

        gResultNamespacesListModel = new NamespacesList();
        gResultNamespacesListModel.setItemMetaData(metadata);

        gResultUsersListModel = UsersModel.getResultUsersList();


        List<Status> map = new ArrayList<>();
        map.add(new Status());
        map.add(new Status());
//        map.put("running", "0");
//        map.put("failed", "0");

        gFinalOverviewResultModel = new Overview();

        gFinalOverviewResultModel.setNamespacesCount(1);
        gFinalOverviewResultModel.setDeploymentsCount(0);
        gFinalOverviewResultModel.setPodsCount(0);
        gFinalOverviewResultModel.setUsersCount(gResultUsersListModel.getItems().size());
        gFinalOverviewResultModel.setDeploymentsUsage(map);
        gFinalOverviewResultModel.setPodsUsage(map);
        gFinalOverviewResultModel.setReplicaSetsUsage(map);

        gFinalOverviewAllResultModel = new Overview();

        gFinalOverviewAllResultModel.setNamespacesCount(0);
        gFinalOverviewAllResultModel.setDeploymentsCount(0);
        gFinalOverviewAllResultModel.setPodsCount(0);
        gFinalOverviewAllResultModel.setUsersCount(gResultUsersListModel.getItems().size());
        gFinalOverviewAllResultModel.setDeploymentsUsage(map);
        gFinalOverviewAllResultModel.setPodsUsage(map);
        gFinalOverviewAllResultModel.setReplicaSetsUsage(map);

        gParams = new Params();
        gParams.setNamespace(NAMESPACE);
        gParams.setOrder("desc");
        gParams.setSelectorType(Constants.RESOURCE_NAMESPACE);

        gResultPodsMetricsListModel = new PodsMetricsList();

    }

    @Test
    public void getOverviewAll() {
        when(propertyService.getDefaultNamespace())
                .thenReturn(NAMESPACE);
        when(namespacesService.getNamespacesList(gParams))
                .thenReturn(gResultNamespacesListModel);
        when(deploymentsService.getDeploymentsList(gParams))
                .thenReturn(gResultDeploymentsListModel);
        when(podsService.getPodsList(gParams))
                .thenReturn(gResultPodsListModel);
        when(metricsService.getPodsMetricsList(gParams)).thenReturn(gResultPodsMetricsListModel);
        when(usersService.getAllUsersListByClusterAndNamespaces(gParams)).thenReturn(gResultUsersListModel);
        when(replicaSetsService.getReplicaSetsList(gParams))
                .thenReturn(gResultReplicaSetsListModel);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE.
                replace("{cluster:.+}", CLUSTER).replace("{namespace:.+}", NAMESPACE), HttpMethod.GET, null, UsersList.class, gParams))
                .thenReturn(gResultUsersListModel);
        when(commonService.setResultModelWithNextUrl(gFinalOverviewAllResultModel, Constants.RESULT_STATUS_SUCCESS, "EMPTY"))
                .thenReturn(gFinalOverviewAllResultModel);

        overviewService.getOverviewAll(gParams);

    }

    @Test
    public void getOverview() {
        when(deploymentsService.getDeploymentsList(gParams))
                .thenReturn(gResultDeploymentsListModel);
        when(podsService.getPodsList(gParams))
                .thenReturn(gResultPodsListModel);
        when(replicaSetsService.getReplicaSetsList(gParams))
                .thenReturn(gResultReplicaSetsListModel);

        when(restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE.
                replace("{cluster:.+}", CLUSTER).replace("{namespace:.+}", NAMESPACE), HttpMethod.GET, null, UsersList.class, gParams))
                .thenReturn(gResultUsersListModel);

        when(commonService.setResultModel(gFinalOverviewResultModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gFinalOverviewResultModel);

    }




}