package org.container.platform.api.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Property Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
@Data
public class PropertyService {

    @Value("${cpMaster.api.access}")
    private String cpMasterApiAccessUrl;

    @Value("${commonApi.url}")
    private String commonApiUrl;

    @Value("${cpTerraman.api.url}")
    private String terramanApiUrl;

    @Value("${cpNamespace.exceptNamespace}")
    List<String> exceptNamespaceList;

    @Value("${cpResource.clusterResource}")
    private String adminResource;

    @Value("${cpNamespace.defaultNamespace}")
    private String defaultNamespace;


    @Value("${cpNamespace.clusterAdminNamespace}")
    private String clusterAdminNamespace;


    @Value("${cpNamespace.role.list}")
    List<String> rolesList;

    @Value("${cpNamespace.role.init}")
    private String initRole;

    @Value("${cpNamespace.role.admin}")
    private String adminRole;

    @Value("${cpNamespace.resourceQuotas.list}")
    List<String> resourceQuotasList;

    @Value("${cpNamespace.resourceQuotas.low}")
    private String lowResourceQuotas;

    @Value("${cpNamespace.resourceQuotas.medium}")
    private String mediumResourceQuotas;

    @Value("${cpNamespace.resourceQuotas.high}")
    private String highResourceQuotas;

    @Value("${cpNamespace.limitRanges.list}")
    List<String> limitRangesList;

    @Value("${cpNamespace.limitRanges.low}")
    private String lowLimitRanges;

    @Value("${cpNamespace.limitRanges.medium}")
    private String mediumLimitRanges;

    @Value("${cpNamespace.limitRanges.high}")
    private String highLimitRanges;

    @Value("${cpAnnotations.configuration}")
    List<String> cpAnnotationsConfiguration;

    @Value("${cpAnnotations.last-applied}")
    String cpAnnotationsLastApplied;

    @Value("${cpNamespace.trafficPolicy.namespace}")
    private String cpTrafficPolicyNamespace;

    @Value("${cpNamespace.trafficPolicy.labels.managed}")
    private String cpTrafficPolicyManagedLabels;

    @Value("${cpNamespace.trafficPolicy.labels.allowed}")
    private String cpTrafficPolicyAllowedLabels;

    //service
    @Value("${cpMaster.api.list.services.list}")
    private String cpMasterApiListServicesListUrl;

    @Value("${cpMaster.api.list.services.get}")
    private String cpMasterApiListServicesGetUrl;

    @Value("${cpMaster.api.list.services.create}")
    private String cpMasterApiListServicesCreateUrl;

    @Value("${cpMaster.api.list.services.delete}")
    private String cpMasterApiListServicesDeleteUrl;

    @Value("${cpMaster.api.list.services.update}")
    private String cpMasterApiListServicesUpdateUrl;

    @Value("${cpMaster.api.list.services.listAllNamespaces}")
    private String cpMasterApiListServicesListAllNamespacesUrl;

    //endpoint
    @Value("${cpMaster.api.list.endpoints.list}")
    private String cpMasterApiListEndpointsListUrl;

    @Value("${cpMaster.api.list.endpoints.get}")
    private String cpMasterApiListEndpointsGetUrl;

    @Value("${cpMaster.api.list.endpoints.listAllNamespaces}")
    private String cpMasterApiListEndpointsListAllNamespacesUrl;

    //pod
    @Value("${cpMaster.api.list.pods.list}")
    private String cpMasterApiListPodsListUrl;

    @Value("${cpMaster.api.list.pods.get}")
    private String cpMasterApiListPodsGetUrl;

    @Value("${cpMaster.api.list.pods.create}")
    private String cpMasterApiListPodsCreateUrl;

    @Value("${cpMaster.api.list.pods.delete}")
    private String cpMasterApiListPodsDeleteUrl;

    @Value("${cpMaster.api.list.pods.update}")
    private String cpMasterApiListPodsUpdateUrl;

    @Value("${cpMaster.api.list.pods.listAllNamespaces}")
    private String cpMasterApiListPodsListAllNamespacesUrl;

    //node
    @Value("${cpMaster.api.list.nodes.list}")
    private String cpMasterApiListNodesListUrl;

    @Value("${cpMaster.api.list.nodes.get}")
    private String cpMasterApiListNodesGetUrl;

    //replicaSets
    @Value("${cpMaster.api.list.replicaSets.list}")
    private String cpMasterApiListReplicaSetsListUrl;

    @Value("${cpMaster.api.list.replicaSets.get}")
    private String cpMasterApiListReplicaSetsGetUrl;

    @Value("${cpMaster.api.list.replicaSets.delete}")
    private String cpMasterApiListReplicaSetsDeleteUrl;

    @Value("${cpMaster.api.list.replicaSets.create}")
    private String cpMasterApiListReplicaSetsCreateUrl;

    @Value("${cpMaster.api.list.replicaSets.update}")
    private String cpMasterApiListReplicaSetsUpdateUrl;

    @Value("${cpMaster.api.list.replicaSets.listAllNamespaces}")
    private String cpMasterApiListReplicaSetsListAllNamespacesUrl;

    //persistentVolumes
    @Value("${cpMaster.api.list.persistentVolumes.list}")
    private String cpMasterApiListPersistentVolumesListUrl;

    @Value("${cpMaster.api.list.persistentVolumes.get}")
    private String cpMasterApiListPersistentVolumesGetUrl;

    @Value("${cpMaster.api.list.persistentVolumes.create}")
    private String cpMasterApiListPersistentVolumesCreateUrl;

    @Value("${cpMaster.api.list.persistentVolumes.delete}")
    private String cpMasterApiListPersistentVolumesDeleteUrl;

    @Value("${cpMaster.api.list.persistentVolumes.update}")
    private String cpMasterApiListPersistentVolumesUpdateUrl;

    //persistentVolumeClaims
    @Value("${cpMaster.api.list.persistentVolumeClaims.list}")
    private String cpMasterApiListPersistentVolumeClaimsListUrl;

    @Value("${cpMaster.api.list.persistentVolumeClaims.get}")
    private String cpMasterApiListPersistentVolumeClaimsGetUrl;

    @Value("${cpMaster.api.list.persistentVolumeClaims.create}")
    private String cpMasterApiListPersistentVolumeClaimsCreateUrl;

    @Value("${cpMaster.api.list.persistentVolumeClaims.delete}")
    private String cpMasterApiListPersistentVolumeClaimsDeleteUrl;

    @Value("${cpMaster.api.list.persistentVolumeClaims.update}")
    private String cpMasterApiListPersistentVolumeClaimsUpdateUrl;

    @Value("${cpMaster.api.list.persistentVolumeClaims.listAllNamespaces}")
    private String cpMasterApiListPersistentVolumeClaimsListAllNamespacesUrl;

    //storageClasses
    @Value("${cpMaster.api.list.storageClasses.list}")
    private String cpMasterApiListStorageClassesListUrl;

    @Value("${cpMaster.api.list.storageClasses.get}")
    private String cpMasterApiListStorageClassesGetUrl;

    @Value("${cpMaster.api.list.storageClasses.create}")
    private String cpMasterApiListStorageClassesCreateUrl;

    @Value("${cpMaster.api.list.storageClasses.delete}")
    private String cpMasterApiListStorageClassesDeleteUrl;

    @Value("${cpMaster.api.list.storageClasses.update}")
    private String cpMasterApiListStorageClassesUpdateUrl;

    //event
    @Value("${cpMaster.api.list.events.list}")
    private String cpMasterApiListEventsListUrl;

    @Value("${cpMaster.api.list.events.get}")
    private String cpMasterApiListEventsGetUrl;

    @Value("${cpMaster.api.list.events.listAllNamespaces}")
    private String cpMasterApiListEventsListAllNamespacesUrl;

    //role
    @Value("${cpMaster.api.list.roles.list}")
    private String cpMasterApiListRolesListUrl;

    @Value("${cpMaster.api.list.roles.get}")
    private String cpMasterApiListRolesGetUrl;

    @Value("${cpMaster.api.list.roles.create}")
    private String cpMasterApiListRolesCreateUrl;

    @Value("${cpMaster.api.list.roles.delete}")
    private String cpMasterApiListRolesDeleteUrl;

    @Value("${cpMaster.api.list.roles.update}")
    private String cpMasterApiListRolesUpdateUrl;

    @Value("${cpMaster.api.list.roles.listAllNamespaces}")
    private String cpMasterApiListRolesListAllNamespacesUrl;

    //deployments
    @Value("${cpMaster.api.list.deployments.list}")
    private String cpMasterApiListDeploymentsListUrl;

    @Value("${cpMaster.api.list.deployments.get}")
    private String cpMasterApiListDeploymentsGetUrl;

    @Value("${cpMaster.api.list.deployments.create}")
    private String cpMasterApiListDeploymentsCreateUrl;

    @Value("${cpMaster.api.list.deployments.delete}")
    private String cpMasterApiListDeploymentsDeleteUrl;

    @Value("${cpMaster.api.list.deployments.update}")
    private String cpMasterApiListDeploymentsUpdateUrl;

    @Value("${cpMaster.api.list.deployments.listAllNamespaces}")
    private String cpMasterApiListDeploymentsListAllNamespacesUrl;

    //rolebinding
    @Value("${cpMaster.api.list.roleBindings.list}")
    private String cpMasterApiListRoleBindingsListUrl;

    @Value("${cpMaster.api.list.roleBindings.get}")
    private String cpMasterApiListRoleBindingsGetUrl;

    @Value("${cpMaster.api.list.roleBindings.create}")
    private String cpMasterApiListRoleBindingsCreateUrl;

    @Value("${cpMaster.api.list.roleBindings.delete}")
    private String cpMasterApiListRoleBindingsDeleteUrl;

    @Value("${cpMaster.api.list.roleBindings.update}")
    private String cpMasterApiListRoleBindingsUpdateUrl;

    @Value("${cpMaster.api.list.roleBindings.listAllNamespaces}")
    private String cpMasterApiListRoleBindingsListAllNamespacesUrl;

    //user
    @Value("${cpMaster.api.list.users.list}")
    private String cpMasterApiListUsersListUrl;

    @Value("${cpMaster.api.list.users.get}")
    private String cpMasterApiListUsersGetUrl;

    @Value("${cpMaster.api.list.users.create}")
    private String cpMasterApiListUsersCreateUrl;

    @Value("${cpMaster.api.list.users.delete}")
    private String cpMasterApiListUsersDeleteUrl;

    @Value("${cpMaster.api.list.users.listAllNamespaces}")
    private String cpMasterApiListUsersListAllNamespacesUrl;

    //namespace
    @Value("${cpMaster.api.list.namespaces.list}")
    private String cpMasterApiListNamespacesListUrl;

    @Value("${cpMaster.api.list.namespaces.get}")
    private String cpMasterApiListNamespacesGetUrl;

    @Value("${cpMaster.api.list.namespaces.create}")
    private String cpMasterApiListNamespacesCreateUrl;

    @Value("${cpMaster.api.list.namespaces.delete}")
    private String cpMasterApiListNamespacesDeleteUrl;

    @Value("${cpMaster.api.list.namespaces.update}")
    private String cpMasterApiListNamespacesUpdateUrl;

    //resourceQuotas
    @Value("${cpMaster.api.list.resourceQuotas.list}")
    private String cpMasterApiListResourceQuotasListUrl;

    @Value("${cpMaster.api.list.resourceQuotas.get}")
    private String cpMasterApiListResourceQuotasGetUrl;

    @Value("${cpMaster.api.list.resourceQuotas.create}")
    private String cpMasterApiListResourceQuotasCreateUrl;

    @Value("${cpMaster.api.list.resourceQuotas.delete}")
    private String cpMasterApiListResourceQuotasDeleteUrl;

    @Value("${cpMaster.api.list.resourceQuotas.update}")
    private String cpMasterApiListResourceQuotasUpdateUrl;

    @Value("${cpMaster.api.list.resourceQuotas.listAllNamespaces}")
    private String cpMasterApiListResourceQuotasListAllNamespacesUrl;

    //secret
    @Value("${cpMaster.api.list.secrets.list}")
    private String cpMasterApiListSecretsListUrl;

    @Value("${cpMaster.api.list.secrets.get}")
    private String cpMasterApiListSecretsGetUrl;

    @Value("${cpMaster.api.list.secrets.create}")
    private String cpMasterApiListSecretsCreateUrl;

    @Value("${cpMaster.api.list.secrets.delete}")
    private String cpMasterApiListSecretsDeleteUrl;

    @Value("${cpMaster.api.list.secrets.update}")
    private String cpMasterApiListSecretsUpdateUrl;

    @Value("${cpMaster.api.list.secrets.listAllNamespaces}")
    private String cpMasterApiListSecretsListAllNamespacesUrl;

    // token
    @Value("${cpMaster.api.list.tokens.create}")
    private String cpMasterApiListTokensCreateUrl;

    //clusterRoleBindings
    @Value("${cpMaster.api.list.clusterRoleBindings.create}")
    private String cpMasterApiListClusterRoleBindingsCreateUrl;

    @Value("${cpMaster.api.list.clusterRoleBindings.delete}")
    private String cpMasterApiListClusterRoleBindingsDeleteUrl;

    //limitRanges
    @Value("${cpMaster.api.list.limitRanges.create}")
    private String cpMasterApiListLimitRangesCreateUrl;

    @Value("${cpMaster.api.list.limitRanges.list}")
    private String cpMasterApiListLimitRangesListUrl;

    @Value("${cpMaster.api.list.limitRanges.get}")
    private String cpMasterApiListLimitRangesGetUrl;

    @Value("${cpMaster.api.list.limitRanges.delete}")
    private String cpMasterApiListLimitRangesDeleteUrl;

    @Value("${cpMaster.api.list.limitRanges.update}")
    private String cpMasterApiListLimitRangesUpdateUrl;

    @Value("${cpMaster.api.list.limitRanges.listAllNamespaces}")
    private String cpMasterApiListLimitRangesListAllNamespacesUrl;

    //configmaps
    @Value("${cpMaster.api.list.configMaps.list}")
    private String cpMasterApiListConfigMapsListUrl;

    @Value("${cpMaster.api.list.configMaps.get}")
    private String cpMasterApiListConfigMapsGetUrl;

    @Value("${cpMaster.api.list.configMaps.create}")
    private String cpMasterApiListConfigMapsCreateUrl;

    @Value("${cpMaster.api.list.configMaps.delete}")
    private String cpMasterApiListConfigMapsDeleteUrl;

    @Value("${cpMaster.api.list.configMaps.update}")
    private String cpMasterApiListConfigMapsUpdateUrl;

    @Value("${cpMaster.api.list.configMaps.listAllNamespaces}")
    private String cpMasterApiListConfigMapsListAllNamespacesUrl;

    //ingresses
    @Value("${cpMaster.api.list.ingresses.list}")
    private String cpMasterApiListIngressesListUrl;

    @Value("${cpMaster.api.list.ingresses.get}")
    private String cpMasterApiListIngressesGetUrl;

    @Value("${cpMaster.api.list.ingresses.create}")
    private String cpMasterApiListIngressesCreateUrl;

    @Value("${cpMaster.api.list.ingresses.delete}")
    private String cpMasterApiListIngressesDeleteUrl;

    @Value("${cpMaster.api.list.ingresses.update}")
    private String cpMasterApiListIngressesUpdateUrl;

    @Value("${cpMaster.api.list.ingresses.listAllNamespaces}")
    private String cpMasterApiListIngressesListAllNamespacesUrl;

    @Value("${vault.secretsEngines.kv.base}")
    private String vaultSecretsEnginesKvBasePath;

    @Value("${vault.secretsEngines.kv.cluster-list}")
    private String vaultSecretsEnginesKvClusterListPath;

    @Value("${vault.secretsEngines.kv.cluster-token}")
    private String vaultSecretsEnginesKvClusterTokenPath;

    @Value("${vault.secretsEngines.kv.super-admin-token}")
    private String vaultSecretsEnginesKvSuperAdminTokenPath;

    @Value("${vault.secretsEngines.kv.user-token}")
    private String vaultSecretsEnginesKvUserTokenPath;

    //clusters
    @Value("${vault.secretsEngines.kv.provider-credential}")
    private String vaultSecretsEnginesKvProviderCredentialPath;

    //ssh key
    @Value("${vault.secretsEngines.kv.ssh-key}")
    private String vaultSecretsEnginesKvSshKeyPath;

    @Value("${cpTerraman.template.path}")
    private String cpTerramanTemplatePath;

    // metrics api
    @Value("${cpMaster.api.metrics.node.list}")
    private String cpMasterApiMetricsNodesListUrl;

    @Value("${cpMaster.api.metrics.node.get}")
    private String cpMasterApiMetricsNodesGetUrl;

    @Value("${cpMaster.api.metrics.pod.list}")
    private String cpMasterApiMetricsPodsListUrl;

    @Value("${cpMaster.api.metrics.pod.get}")
    private String cpMasterApiMetricsPodsGetUrl;


    // cp metric collector
    @Value("${cpMetricCollector.api.url}")
    private String cpMetricCollectorApiUrl;

    @Value("${cpMetricCollector.api.clusters.get}")
    private String cpMetricCollectorApiClustersGetUrl;

    @Value("${cpMetricCollector.api.clusters.key}")
    private String cpMetricCollectorApiClustersKey;
}