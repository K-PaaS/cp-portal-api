package org.container.platform.api.clusters.clusters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.clusters.clusters.support.ClusterInfo;
import org.container.platform.api.clusters.nodes.NodesList;
import org.container.platform.api.clusters.nodes.NodesService;
import org.container.platform.api.clusters.nodes.support.NodesListItem;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
import org.container.platform.api.exception.ResultStatusException;
import org.container.platform.api.users.Users;
import org.container.platform.api.users.UsersList;
import org.container.platform.api.users.UsersService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.support.VaultResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ClustersServiceTest {
    private static final String CLUSTER_API_URL = "https://localhost:6443";
    private static final String CLUSTER_NAME = "cp-cluster";
    private static final String CLUSTER_TOKEN = "";
    private static final String CLUSTER_TYPE = "host";
    private static final Constants.ProviderType PROVIDER_TYPE = Constants.ProviderType.AWS;
    private static final String CLUSTER_DESCRIPTION = "";
    private static final String VAULT_PATH = "";

    private static Clusters gParamsModel = null;
    private static Clusters gResultModel = null;
    private static Clusters gFinalResultModel = null;
    private static Clusters vaultClusterModel = null;

    private static ClustersList gResultListModel = null;
    private static ClustersList gFinalResultListModel = null;

    private static Params gParams = null;
    private static VaultResponse gVaultResultModel = null;

    private static ResultStatus gResultStatus = null;

    private static UsersList gUsersList = null;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private VaultService vaultService;

    @Mock
    private CommonService commonService;

    @Mock
    private NodesService nodesService;

    @Mock
    private PropertyService propertyService;

    @Mock
    private UsersService usersService;

    @Mock
    private ClustersService clustersServiceMock;

    @InjectMocks
    private ClustersService clustersService;

    @Before
    public void setUp() {
        gResultModel = new Clusters();
        gResultModel.setName(CLUSTER_NAME);
        gResultModel.setIsActive(true);
        gResultModel.setStatus(Constants.ClusterStatus.ACTIVE.getInitial());

        gParamsModel = new Clusters();
        gParamsModel.setName(CLUSTER_NAME);
        gParamsModel.setClusterId(CLUSTER_NAME);
        gParamsModel.setClusterType(CLUSTER_TYPE);
        gParamsModel.setProviderType(PROVIDER_TYPE);
        gParamsModel.setDescription(CLUSTER_DESCRIPTION);

        gResultListModel = new ClustersList();
        List<Clusters> gListItemModel = new ArrayList<>();
        gListItemModel.add(gResultModel);
        gResultListModel.setItems(gListItemModel);

        gFinalResultListModel = new ClustersList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListModel.setItems(gListItemModel);

        gFinalResultModel = new Clusters();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        vaultClusterModel = new Clusters();
        vaultClusterModel.setClusterApiUrl(CLUSTER_API_URL);
        vaultClusterModel.setClusterToken(CLUSTER_TOKEN);

        gResultStatus = new ResultStatus();
        gResultStatus.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gParams = new Params();

        gParams.setCluster(CLUSTER_NAME);
        gParams.setIsClusterRegister(true);
        gParams.setUserAuthId("test");
        gParams.setUserType(Constants.AUTH_SUPER_ADMIN);

        gParams.setCluster(CLUSTER_NAME);
        gParams.setResourceName(CLUSTER_NAME);
        gParams.setClusterType(CLUSTER_TYPE);
        gParams.setProviderType(PROVIDER_TYPE);
        gParams.setDescription("Describtion");
        gParams.setIsClusterRegister(false);
        gParams.setCloudAccountId("cloudAccountId");

        gVaultResultModel = new VaultResponse();


        gUsersList = new UsersList();
        List<Users> gUsersItems = new ArrayList<>();
        gUsersList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gUsersList.setItems(gUsersItems);


    }

    /**
     * Clusters 정보 저장(Create Clusters Info) Test
     */
    @Test
    public void createClusters_Valid_ReturnModel() {
        // given
        Clusters clusters = new Clusters();
        clusters.setName(gParams.getResourceName());

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(gParams.getCluster());
        clusterInfo.setClusterApiUrl(vaultClusterModel.getClusterApiUrl());
        clusterInfo.setClusterToken(CLUSTER_TOKEN);


        when(vaultService.getClusterDetails(gParams.getCluster())).thenReturn(null);
        when(propertyService.getVaultSecretsEnginesKvClusterTokenPath()).thenReturn("secret/cluster/cp-cluster");
        when(vaultService.write("secret/cluster/cp-cluster", clusterInfo)).thenReturn(null);
        when(propertyService.getCpTerramanTemplatePath()).thenReturn("/tmp/terraform/cp-cluster/cp-cluster.tf");
//        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, gParams)).thenReturn(gFinalResultModel);
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        Clusters result = new Clusters();
        try {
            result = clustersService.createClusters(gParams);
        } catch (Exception e) {}
        // when

        // then
        assertEquals(null, result.getResultCode());
    }

    @Test
    public void createClusters_register_INVALID_ReturnModel() {
        // given
        Clusters clusters = new Clusters();
        clusters.setName(gParams.getResourceName());

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(gParams.getCluster());
        clusterInfo.setClusterApiUrl(vaultClusterModel.getClusterApiUrl());
        clusterInfo.setClusterToken(CLUSTER_TOKEN);
        gParams.setIsClusterRegister(true);

        when(vaultService.getClusterDetails(gParams.getCluster())).thenReturn(new Clusters());

        Clusters result = new Clusters();
        result.setResultCode(Constants.RESULT_STATUS_FAIL);
        try {
            // when
            result = clustersService.createClusters(gParams);
        } catch (Exception e) {}

    }

    @Test
    public void createClusters_create_file_exception_ReturnModel() {
        // given
        Clusters clusters = new Clusters();
        clusters.setName(gParams.getResourceName());

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(gParams.getCluster());
        clusterInfo.setClusterApiUrl(vaultClusterModel.getClusterApiUrl());
        clusterInfo.setClusterToken(CLUSTER_TOKEN);
        gParams.setIsClusterRegister(false);
        gParams.setCluster(CLUSTER_NAME);

        when(propertyService.getCpTerramanTemplatePath()).thenReturn("test_path");
        when(vaultService.getClusterDetails(gParams.getCluster())).thenReturn(new Clusters());

        try {
            // when
            Clusters result = clustersService.createClusters(gParams);
        } catch (Exception e) {}

        // then
    }

    /**
     * Clusters 정보 조회(Get Clusters Info) Test
     */
    @Test
    public void getClusters_Valid_ReturnModel() {
        // given


        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + gParams.getCluster(), HttpMethod.GET, null, Clusters.class, gParams)).thenReturn(gResultModel);
        when(commonService.getKubernetesInfo(gParams)).thenReturn(vaultClusterModel);
        when(restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(nodesService.getNodesList(gParams)).thenReturn(new NodesList());
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.getClusters(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getClustersList_Invalid_ReturnModel() {
        // given
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/users/{userAuthId}?userType={userType}"
                .replace("{userAuthId}", gParams.getUserAuthId())
                .replace("{userType}", gParams.getUserType()), HttpMethod.GET, null, ClustersList.class, gParams)).thenReturn(gResultListModel);
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + gParams.getCluster(), HttpMethod.GET, null, Clusters.class, gParams)).thenReturn(gResultModel);
        when(commonService.getKubernetesInfo(gParams)).thenReturn(vaultClusterModel);
        when(restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, gParams)).thenReturn(gResultStatus);
//        when(nodesService.getNodesList(gParams)).thenReturn(nodeList);
        when(commonService.globalListProcessing(gResultListModel, gParams, ClustersList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        ClustersList result = clustersService.getClustersList(gParams);
        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getClusters_ReturnModel() {
        // given
        NodesList nodeList = new NodesList();
        List<NodesListItem> itemList = new ArrayList<>();
        NodesListItem item = new NodesListItem();
        CommonMetaData metaData = new CommonMetaData();
        Map<String, String> label = new HashMap<>();
        label.put("node-role.kubernetes.io/control-plane", "version");
        metaData.setLabels(label);
        CommonStatus status = new CommonStatus();
        CommonNodeInfo nodeInfo = new CommonNodeInfo();
        nodeInfo.setKubeletVersion("version");
        status.setNodeInfo(nodeInfo);
        item.setStatus(status);
        item.setMetadata(metaData);
        itemList.add(item);
        nodeList.setItems(itemList);
        gResultModel.setStatus(Constants.ClusterStatus.ACTIVE.getInitial());
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/users/{userAuthId}?userType={userType}"
                .replace("{userAuthId}", gParams.getUserAuthId())
                .replace("{userType}", gParams.getUserType()), HttpMethod.GET, null, ClustersList.class, gParams)).thenReturn(gResultListModel);
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + gParams.getCluster(), HttpMethod.GET, null, Clusters.class, gParams)).thenReturn(gResultModel);
        when(commonService.getKubernetesInfo(gParams)).thenReturn(vaultClusterModel);
        when(restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, gParams)).thenReturn(gResultStatus);

        when(nodesService.getNodesList(new Params(gResultModel.getClusterId(), gResultModel.getName()))).thenReturn(nodeList);
        when(commonService.globalListProcessing(gResultListModel, gParams, ClustersList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        Clusters result = clustersService.getClusters(gParams);
        // then
    }

    @Test
    public void createClusterInfoToVault_Valid_ReturnModel() {
        when(vaultService.getClusterDetails(gParams.getCluster())).thenReturn(null);
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(gParams.getCluster());
        clusterInfo.setClusterApiUrl(vaultClusterModel.getClusterApiUrl());
        when(propertyService.getVaultSecretsEnginesKvClusterTokenPath()).thenReturn("secret/cluster/cp-cluster");
        when(vaultService.write("secret/cluster/cp-cluster", clusterInfo)).thenReturn(null);


        Boolean result = clustersService.createClusterInfoToVault(gParams);

        assertEquals(result, true);

    }

    @Test
    public void updateClusters_Valid_ReturnModel() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.PATCH, setClusters(gParams), Clusters.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        Clusters result = clustersService.updateClusters(gParams);

        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void deleteClusters_NotValid_ReturnModel() {
        when(usersService.getUsersListByCluster(gParams)).thenReturn(gUsersList);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/clusters/{id}".replace("{id}", gParams.getCluster()),
                HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatus);

        Clusters result = new Clusters();
        try {
            result = clustersService.deleteClusters(gParams);

        }catch(ResultStatusException e) {

        }
        // then
        assertEquals(null, result.getResultCode());
    }

    private Clusters setClusters(Params gParams) {
        Clusters clusters = new Clusters();
        clusters.setClusterId(gParams.getCluster());
        clusters.setName(gParams.getResourceName());
        clusters.setClusterType(gParams.getClusterType());
        clusters.setProviderType(gParams.getProviderType());
        clusters.setDescription(gParams.getDescription());

        return clusters;
    }

}
