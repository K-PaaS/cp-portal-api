package org.paasta.container.platform.api.overview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesListAdmin;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesService;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersModel;
import org.paasta.container.platform.api.users.UsersService;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsListAdmin;
import org.paasta.container.platform.api.workloads.deployments.DeploymentsService;
import org.paasta.container.platform.api.workloads.pods.PodsListAdmin;
import org.paasta.container.platform.api.workloads.pods.PodsService;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsListAdmin;
import org.paasta.container.platform.api.workloads.replicaSets.ReplicaSetsService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
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

    private static DeploymentsListAdmin gResultDeploymentsListAdminModel = null;

    private static PodsListAdmin gResultPodsListAdminModel = null;

    private static ReplicaSetsListAdmin gResultReplicaSetsListAdminModel = null;

    private static NamespacesListAdmin gResultNamespacesListAdminModel = null;

    private static UsersList gResultUsersListModel = null;

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

        gResultDeploymentsListAdminModel = new DeploymentsListAdmin();
        gResultDeploymentsListAdminModel.setItemMetaData(metadata);

        gResultPodsListAdminModel = new PodsListAdmin();
        gResultPodsListAdminModel.setItemMetaData(metadata);

        gResultReplicaSetsListAdminModel = new ReplicaSetsListAdmin();
        gResultReplicaSetsListAdminModel.setItemMetaData(metadata);

        gResultNamespacesListAdminModel = new NamespacesListAdmin();
        gResultNamespacesListAdminModel.setItemMetaData(metadata);

        gResultUsersListModel = UsersModel.getResultUsersList();


        Map<String, Object> map = new HashMap<>();
        map.put("running", "0");
        map.put("failed", "0");

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
    }

    @Test
    public void getOverviewAll() {
        when(propertyService.getDefaultNamespace())
                .thenReturn(NAMESPACE);
        when(namespacesService.getNamespacesListAdmin(0,0,"creationTime", "desc", ""))
                .thenReturn(gResultNamespacesListAdminModel);
        when(deploymentsService.getDeploymentsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultDeploymentsListAdminModel);
        when(podsService.getPodsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultPodsListAdminModel);
        when(replicaSetsService.getReplicaSetsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultReplicaSetsListAdminModel);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE.
                replace("{cluster:.+}", CLUSTER).replace("{namespace:.+}", NAMESPACE), HttpMethod.GET, null, UsersList.class))
                .thenReturn(gResultUsersListModel);
        when(commonService.setResultModelWithNextUrl(gFinalOverviewAllResultModel, Constants.RESULT_STATUS_SUCCESS, "EMPTY"))
                .thenReturn(gFinalOverviewAllResultModel);

    }

    @Test
    public void getOverview() {
        when(deploymentsService.getDeploymentsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultDeploymentsListAdminModel);
        when(podsService.getPodsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultPodsListAdminModel);
        when(replicaSetsService.getReplicaSetsListAdmin(NAMESPACE,0,0,"creationTime", "desc", ""))
                .thenReturn(gResultReplicaSetsListAdminModel);

        when(restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE.
                replace("{cluster:.+}", CLUSTER).replace("{namespace:.+}", NAMESPACE), HttpMethod.GET, null, UsersList.class))
                .thenReturn(gResultUsersListModel);

        when(commonService.setResultModel(gFinalOverviewResultModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gFinalOverviewResultModel);

    }

    @Test
    public void getOverviewAdmin() {
        when(deploymentsService.getDeploymentsListAllNamespacesAdmin(0,0,"creationTime", "desc", ""))
                .thenReturn(gResultDeploymentsListAdminModel);
        when(podsService.getPodsListAllNamespacesAdmin(0,0,"creationTime", "desc", ""))
                .thenReturn(gResultPodsListAdminModel);
        when(replicaSetsService.getReplicaSetsListAllNamespacesAdmin(0,0,"creationTime", "desc", ""))
                .thenReturn(gResultReplicaSetsListAdminModel);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_USERS_LIST_BY_NAMESPACE.
                replace("{cluster:.+}", CLUSTER).replace("{namespace:.+}", NAMESPACE), HttpMethod.GET, null, UsersList.class))
                .thenReturn(gResultUsersListModel);
        when(commonService.setResultModel(gFinalOverviewResultModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gFinalOverviewResultModel);


    }
}