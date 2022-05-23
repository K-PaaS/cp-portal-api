package org.paasta.container.platform.api.workloads.replicaSets;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReplicaSets Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.09.10
 */
@Service
public class ReplicaSetsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new ReplicaSet service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public ReplicaSetsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the replicaSets list
     */
    public ReplicaSetsList getReplicaSetsList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {

        HashMap resultMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl()
                        .replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        ReplicaSetsList replicaSetsList = commonService.setResultObject(resultMap, ReplicaSetsList.class);
        replicaSetsList = commonService.resourceListProcessing(replicaSetsList, offset, limit, orderBy, order, searchName, ReplicaSetsList.class);

        return (ReplicaSetsList) commonService.setResultModel(replicaSetsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 상세 조회(Get ReplicaSets detail)
     *
     * @param namespace       the namespace
     * @param replicaSetsName the replicaSets name
     * @return the replicaSets detail
     */
    public ReplicaSets getReplicaSets(String namespace, String replicaSetsName) {
        HashMap resultMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", replicaSetsName)
                , HttpMethod.GET, null, Map.class);

        return (ReplicaSets) commonService.setResultModel(commonService.setResultObject(resultMap, ReplicaSets.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets 상세 조회(Get ReplicaSets detail)
     * (Admin Portal)
     *
     * @param namespace       the namespace
     * @param replicaSetsName the replicaSets name
     * @return the replicaSets detail
     */
    public Object getReplicaSetsAdmin(String namespace, String replicaSetsName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", replicaSetsName)
                , HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }
        ReplicaSetsAdmin replicaSetsAdmin = commonService.setResultObject(responseMap, ReplicaSetsAdmin.class);
        replicaSetsAdmin = commonService.annotationsProcessing(replicaSetsAdmin, ReplicaSetsAdmin.class);

        return commonService.setResultModel(replicaSetsAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets YAML 조회(Get ReplicaSets yaml)
     *
     * @param namespace       the namespace
     * @param replicaSetsName the replicaSets name
     * @param resultMap the resultMap
     * @return the replicaSets yaml
     */
    public Object getReplicaSetsYaml(String namespace, String replicaSetsName, HashMap resultMap) {
        String resultString = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", replicaSetsName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets Admin YAML 조회(Get ReplicaSets Admin yaml)
     *
     * @param namespace       the namespace
     * @param replicaSetsName the replicaSets name
     * @param resultMap the resultMap
     * @return the replicaSets yaml
     */
    public Object getReplicaSetsAdminYaml(String namespace, String replicaSetsName, HashMap resultMap) {
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", replicaSetsName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets Selector)
     *
     * @param namespace          the namespace
     * @param selectors          the selectors
     * @param type the type
     * @param ownerReferencesName the ownerReferencesName
     * @param ownerReferencesUid the ownerReferencesUid
     * @param offset             the offset
     * @param limit              the limit
     * @param orderBy            the orderBy
     * @param order              the order
     * @param searchName         the searchName
     * @return the replicaSets list
     */
    public ReplicaSetsList getReplicaSetsListLabelSelector(String namespace, String selectors, String type, String ownerReferencesName, String ownerReferencesUid, int offset, int limit, String orderBy, String order, String searchName) {
        String requestSelector = "?labelSelector=" + selectors;

        HashMap resultMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl()
                        .replace("{namespace}", namespace) + requestSelector, HttpMethod.GET, null, Map.class);

        ReplicaSetsList replicaSetsList = commonService.setResultObject(resultMap, ReplicaSetsList.class);

        if (type.equals(Constants.DEPLOYMENTS_FOR_SELECTOR)) {
            if (ownerReferencesUid != null && !ownerReferencesUid.trim().isEmpty()) {
                // selector by deployments Uid
                List<ReplicaSets> replicaSetsItemByUid;
                replicaSetsItemByUid = replicaSetsList.getItems().stream().filter(x -> x.getMetadata().getOwnerReferences().get(0).getUid().matches(ownerReferencesUid)).collect(Collectors.toList());
                replicaSetsList.setItems(replicaSetsItemByUid);
            }

            if (ownerReferencesName != null && !ownerReferencesName.trim().isEmpty()) {
                // selector by deployments Name
                List<ReplicaSets> replicaSetsItemByName;
                replicaSetsItemByName = replicaSetsList.getItems().stream().filter(x -> x.getMetadata().getName().matches("(?i).*" + ownerReferencesName + ".*")).collect(Collectors.toList());
                replicaSetsList.setItems(replicaSetsItemByName);
            }


            // filter by relicas count ( replicas > 0 )
            List<ReplicaSets> replicaSetsItemByReplicasCount;
            replicaSetsItemByReplicasCount = replicaSetsList.getItems().stream().filter(x -> x.getSpec().getReplicas() > 0).collect(Collectors.toList());
            replicaSetsList.setItems(replicaSetsItemByReplicasCount);

        }

        //paging and order process
        replicaSetsList = commonService.resourceListProcessing(replicaSetsList, offset, limit, orderBy, order, searchName, ReplicaSetsList.class);

        //If there are more than one ReplicaSets, extract only one by created time
        if (replicaSetsList.getItems().size() > 1) {
            ReplicaSets extractReplicaSets = replicaSetsList.getItems().get(0);

            List<ReplicaSets> extractReplicaSetsList = new ArrayList<>();
            extractReplicaSetsList.add(extractReplicaSets);
            replicaSetsList.setItems(extractReplicaSetsList);
        }

        return (ReplicaSetsList) commonService.setResultModel(replicaSetsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets Selector)
     * (Admin portal)
     *
     * @param namespace the namespace
     * @param selectors the selectors
     * @return the replicaSets list
     */
    public ReplicaSetsListAdmin getReplicaSetsListLabelSelectorAdmin(String namespace, String selectors) {
        String requestSelector = "?labelSelector=" + selectors;

        HashMap resultMap = (HashMap) restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl()
                        .replace("{namespace}", namespace) + requestSelector, HttpMethod.GET, null, Map.class);

        ReplicaSetsListAdmin replicaSetsListAdmin = commonService.setResultObject(resultMap, ReplicaSetsListAdmin.class);
        replicaSetsListAdmin = commonService.setCommonItemMetaDataBySelector(replicaSetsListAdmin, ReplicaSetsListAdmin.class);

        return (ReplicaSetsListAdmin) commonService.setResultModel(replicaSetsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 목록 조회(Get ReplicaSets list)
     * (Admin Portal)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the replicaSets list
     */
    public Object getReplicaSetsListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        ReplicaSetsListAdmin replicaSetsListAdmin = commonService.setResultObject(responseMap, ReplicaSetsListAdmin.class);
        replicaSetsListAdmin = commonService.resourceListProcessing(replicaSetsListAdmin, offset, limit, orderBy, order, searchName, ReplicaSetsListAdmin.class);

        return commonService.setResultModel(replicaSetsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets 생성(Create ReplicaSets)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public Object createReplicaSets(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS);
    }

    /**
     * ReplicaSets 삭제(Delete ReplicaSets)
     *
     * @param namespace the namespace
     * @param name      the replicaSets name
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public ResultStatus deleteReplicaSets(String namespace, String name, boolean isAdmin) {
        ResultStatus resultStatus;

        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListReplicaSetsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListReplicaSetsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS);
    }

    /**
     * ReplicaSets 수정(Update ReplicaSets)
     *
     * @param namespace the namespace
     * @param name      the replicaSets name
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public ResultStatus updateReplicaSets(String namespace, String name, String yaml, boolean isAdmin) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.PUT, yaml, ResultStatus.class, isAdmin);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS_DETAIL.replace("{replicaSetName:.+}", name));
    }


    /**
     * 전체 Namespaces 의 ReplicaSets Admin 목록 조회(Get ReplicaSets Admin list in all namespaces)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the replicaSets all list
     */
    public Object getReplicaSetsListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        ReplicaSetsListAdmin replicaSetsListAdmin = commonService.setResultObject(responseMap, ReplicaSetsListAdmin.class);
        replicaSetsListAdmin = commonService.resourceListProcessing(replicaSetsListAdmin, offset, limit, orderBy, order, searchName, ReplicaSetsListAdmin.class);

        return commonService.setResultModel(replicaSetsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


}
