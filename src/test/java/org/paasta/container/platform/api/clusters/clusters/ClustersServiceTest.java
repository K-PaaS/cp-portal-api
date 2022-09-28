package org.paasta.container.platform.api.clusters.clusters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.clusters.support.ClusterInfo;
import org.paasta.container.platform.api.clusters.nodes.NodesList;
import org.paasta.container.platform.api.clusters.nodes.NodesService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.support.VaultResponse;

import java.util.ArrayList;
import java.util.List;

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

        gVaultResultModel = new VaultResponse();
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
        when(propertyService.getVaultClusterTokenPath()).thenReturn("secret/cluster/cp-cluster");
        when(vaultService.write("secret/cluster/cp-cluster", clusterInfo)).thenReturn(null);
        when(propertyService.getCpTerramanTemplatePath()).thenReturn("/tmp/terraform/cp-cluster/cp-cluster.tf");
//        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, gParams)).thenReturn(gFinalResultModel);
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.POST, clusters, Clusters.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        Clusters result = clustersService.createClusters(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
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
    public void getClustersList_Valid_ReturnModel() {
        // given
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/users/{userAuthId}?userType={userType}"
                .replace("{userAuthId}", gParams.getUserAuthId())
                .replace("{userType}", gParams.getUserType()), HttpMethod.GET, null, ClustersList.class, gParams)).thenReturn(gResultListModel);
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/" + gParams.getCluster(), HttpMethod.GET, null, Clusters.class, gParams)).thenReturn(gResultModel);
        when(commonService.getKubernetesInfo(gParams)).thenReturn(vaultClusterModel);
        when(restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, gParams)).thenReturn(gResultStatus);
        when(nodesService.getNodesList(gParams)).thenReturn(new NodesList());
        when(commonService.globalListProcessing(gResultListModel, gParams, ClustersList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        ClustersList result = clustersService.getClustersList(gParams);
        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createClusterInfoToVault_Valid_ReturnModel() {
        when(vaultService.getClusterDetails(gParams.getCluster())).thenReturn(null);
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterId(gParams.getCluster());
        clusterInfo.setClusterApiUrl(vaultClusterModel.getClusterApiUrl());
        when(propertyService.getVaultClusterTokenPath()).thenReturn("secret/cluster/cp-cluster");
        when(vaultService.write("secret/cluster/cp-cluster", clusterInfo)).thenReturn(null);


        Boolean result = clustersService.createClusterInfoToVault(gParams);

        assertEquals(result, true);

    }

    @Test
    public void updateClusters_Valid_ReturnModel() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters", HttpMethod.PATCH, gParamsModel, Clusters.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        Clusters result = clustersService.updateClusters(gParams);

        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void deleteClusters_Valid_ReturnModel() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/cp-cluster", HttpMethod.DELETE, null, Clusters.class, gParams)).thenReturn(gFinalResultModel);

        Clusters result = clustersService.deleteClusters(gParams);

        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

}
