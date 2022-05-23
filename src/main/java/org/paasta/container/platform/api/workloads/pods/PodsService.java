package org.paasta.container.platform.api.workloads.pods;


import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonContainer;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.workloads.pods.support.ContainerStatusesItem;
import org.paasta.container.platform.api.workloads.pods.support.PodsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Pods Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.09
 */
@Service
public class PodsService {
    private static final String STATUS_FIELD_NAME = "status";

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Pods service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public PodsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Pods 목록 조회(Get Pods list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the pods list
     */
    public PodsList getPodsList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl()
                        .replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        PodsList podsList = commonService.setResultObject(responseMap, PodsList.class);
        podsList = getPodsMetricList(namespace, podsList);
        podsList = commonService.resourceListProcessing(podsList, offset, limit, orderBy, order, searchName, PodsList.class);

        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Pods Metric 정보 조회(Get Pods Metric List)
     *
     * @param namespace the namespace
     * @param podsList  the podsList
     * @return the pods list
     */
    public PodsList getPodsMetricList(String namespace, PodsList podsList) {
        HashMap responseMap = (HashMap) restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                Constants.URI_METRIC_API_BASIC.replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);
        PodsMetric podsMetrics = commonService.setResultObject(responseMap, PodsMetric.class);

        getMergeMetric(podsList, podsMetrics);

        return podsList;
    }

    /**
     * Pods Metric 정보 병합(Merge Pods Metric List)
     *
     * @param podsList    the podsList
     * @param podsMetrics the podsMetrics
     */
    public void getMergeMetric(PodsList podsList, PodsMetric podsMetrics) {
        Pods pods = null;
        PodsUsage podsUsage = null;
        CommonContainer container = null;
        Containers containerUsage = null;
        HashMap hm = null;
        for (int i = 0; i < podsList.getItems().size(); i++) {
            pods = podsList.getItems().get(i);
            for (int t = 0; t < podsMetrics.getItems().size(); t++) {
                podsUsage = podsMetrics.getItems().get(t);
                if (pods.getMetadata().getName().equals(podsUsage.getMetadata().getName())) {
                    for (int u = 0; u < pods.getSpec().getContainers().size(); u++) {
                        container = pods.getSpec().getContainers().get(u);
                        for (int y = 0; y < podsUsage.getContainers().size(); y++) {
                            containerUsage = podsUsage.getContainers().get(y);
                            if (container.getName().equals(containerUsage.getName())) {
                                hm = new HashMap();
                                hm.put("cpu", containerUsage.getUsage().getCpu());
                                hm.put("memory", containerUsage.getUsage().getMemory());
                                container.getResources().setUsage(hm);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Pods 목록 조회(Get Pods list)
     * (Admin Portal)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the pods list
     */
    public Object getPodsListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        PodsListAdmin podsListAdmin = commonService.setResultObject(responseMap, PodsListAdmin.class);
        podsListAdmin = commonService.resourceListProcessing(podsListAdmin, offset, limit, orderBy, order, searchName, PodsListAdmin.class);
        podsListAdmin = restartCountProcessing(podsListAdmin);


        return commonService.setResultModel(podsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Pods 목록 조회(Get Pods selector)
     *
     * @param namespace the namespace
     * @param selector  the selector
     * @param type the type
     * @param ownerReferencesUid the ownerReferencesUid
     * @param offset the offset
     * @param limit the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the pods list
     */
    PodsList getPodListWithLabelSelector(String namespace, String selector, String type, String ownerReferencesUid, int offset, int limit, String orderBy, String order, String searchName) {
        String requestSelector = "?labelSelector=" + selector;
        HashMap resultMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl().replace("{namespace}", namespace) + requestSelector, HttpMethod.GET, null, Map.class);

        PodsList podsList = commonService.setResultObject(resultMap, PodsList.class);
        podsList = getPodsMetricList(namespace, podsList);

        // selector by replicaSets
        if (type.equals(Constants.REPLICASETS_FOR_SELECTOR)) {
            List<Pods> podsItem;
            podsItem = podsList.getItems().stream().filter(x -> x.getMetadata().getOwnerReferences().get(0).getUid().matches(ownerReferencesUid)).collect(Collectors.toList());
            podsList.setItems(podsItem);
        }

        podsList = commonService.resourceListProcessing(podsList, offset, limit, orderBy, order, searchName, PodsList.class);

        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Pods 목록 조회(Get Pods selector)
     * (Admin portal)
     *
     * @param namespace          the namespace
     * @param selector           the nodeName
     * @param type               the type
     * @param ownerReferencesUid the ownerReferencesUid
     * @param offset             the offset
     * @param limit              the limit
     * @param orderBy            the orderBy
     * @param order              the order
     * @param searchName         the searchName
     * @return the pods list
     */
    public Object getPodListWithLabelSelectorAdmin(String namespace, String selector, String type, String ownerReferencesUid, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        String requestSelector = "?labelSelector=" + selector;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl().replace("{namespace}", namespace) + requestSelector, HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        PodsListAdmin podsListAdmin = commonService.setResultObject(responseMap, PodsListAdmin.class);

        if (type.equals(Constants.REPLICASETS_FOR_SELECTOR)) {
            podsListAdmin = podsFIlterWithOwnerReferences(podsListAdmin, ownerReferencesUid);
        }
        podsListAdmin = commonService.resourceListProcessing(podsListAdmin, offset, limit, orderBy, order, searchName, PodsListAdmin.class);
        podsListAdmin = restartCountProcessing(podsListAdmin);

        return commonService.setResultModel(podsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Node 명에 따른 Pods 목록 조회(Get Pods node)
     *
     * @param namespace the namespace
     * @param nodeName  the node name
     * @param offset             the offset
     * @param limit              the limit
     * @param orderBy            the orderBy
     * @param order              the order
     * @param searchName         the searchName
     * @return the pods list
     */
    PodsList getPodListByNode(String namespace, String nodeName, int offset, int limit, String orderBy, String order, String searchName) {
        String requestURL = propertyService.getCpMasterApiListPodsListUrl().replace("{namespace}", namespace)
                + "?fieldSelector=spec.nodeName=" + nodeName;

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API, requestURL,
                HttpMethod.GET, null, Map.class);

        PodsList podsList = commonService.setResultObject(responseMap, PodsList.class);
        podsList = getPodsMetricList(namespace, podsList);
        podsList = commonService.resourceListProcessing(podsList, offset, limit, orderBy, order, searchName, PodsList.class);

        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Node 명에 따른 Pods 목록 조회(Get Pods node)
     * (Admin portal)
     *
     * @param namespace  the namespace
     * @param nodeName   the node name
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the pods list
     */
    public Object getPodsListByNodeAdmin(String namespace, String nodeName, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;
        String requestURL = null;

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            // if all namespace
            requestURL = propertyService.getCpMasterApiListPodsListAllNamespacesUrl()    // all namespace pods list endpoints
                    + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE) // fieldSelector for exclude namespace
                    + commonService.generateFieldSelectorForPodsByNode(Constants.PARAM_QUERY_AND, nodeName);  // fieldSelector for node name filter
        } else {
            requestURL = propertyService.getCpMasterApiListPodsListUrl().replace("{namespace}", namespace) // pods list endpoints
                    + commonService.generateFieldSelectorForPodsByNode(Constants.PARAM_QUERY_FIRST, nodeName); // fieldSelector for node name filter
        }

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, requestURL, HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        PodsListAdmin podsListAdmin = commonService.setResultObject(responseMap, PodsListAdmin.class);
        podsListAdmin = commonService.resourceListProcessing(podsListAdmin, offset, limit, orderBy, order, searchName, PodsListAdmin.class);
        podsListAdmin = restartCountProcessing(podsListAdmin);

        return commonService.setResultModel(podsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Pods 상세 조회(Get Pods detail)
     *
     * @param namespace the namespace
     * @param podsName  the pods name
     * @return the pods detail
     */
    public Pods getPods(String namespace, String podsName) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl().replace("{namespace}", namespace).replace("{name}", podsName),
                HttpMethod.GET, null, Map.class);

        return (Pods) commonService.setResultModel(commonService.setResultObject(responseMap, Pods.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Pods 상세 조회(Get Pods detail)
     * (Admin Portal)
     *
     * @param namespace the namespace
     * @param podsName  the pods name
     * @return the pods detail
     */
    public Object getPodsAdmin(String namespace, String podsName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", podsName)
                , HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }

        PodsStatus status = commonService.setResultObject(responseMap.get(STATUS_FIELD_NAME), PodsStatus.class);

        if(status.getContainerStatuses() == null) {
            List<ContainerStatusesItem> list = new ArrayList<>();
            ContainerStatusesItem item = new ContainerStatusesItem();
            item.setRestartCount(0);

            list.add(item);
            status.setContainerStatuses(list);
        }

        responseMap.put(STATUS_FIELD_NAME, status);

        PodsAdmin podsAdmin = commonService.setResultObject(responseMap, PodsAdmin.class);
        podsAdmin = commonService.annotationsProcessing(podsAdmin, PodsAdmin.class);

        return commonService.setResultModel(podsAdmin, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Pods YAML 조회(Get Pods yaml)
     *
     * @param namespace the namespace
     * @param podName   the pods name
     * @param resultMap the result map
     * @return the pods yaml
     */
    public Object getPodsYaml(String namespace, String podName, HashMap resultMap) {
        String resultString = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl().replace("{namespace}", namespace).replace("{name}", podName),
                HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Pods Admin YAML 조회(Get Pods Admin yaml)
     *
     * @param namespace the namespace
     * @param podName   the pods name
     * @param resultMap the result map
     * @return the pods yaml
     */
    public Object getPodsAdminYaml(String namespace, String podName, HashMap resultMap) {
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl().replace("{namespace}", namespace).replace("{name}", podName),
                HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Pods 생성(Create Pods)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public Object createPods(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS);
    }

    /**
     * Pods 삭제(Delete Pods)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    public ResultStatus deletePods(String namespace, String resourceName, boolean isAdmin) {
        ResultStatus resultStatus;

        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListPodsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListPodsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS);
    }

    /**
     * Pods 수정(Update Pods)
     *
     * @param namespace the namespace
     * @param name      the pods name
     * @param yaml      the yaml
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    public Object updatePods(String namespace, String name, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.PUT, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS_DETAIL.replace("{podName:.+}", name));
    }


    /**
     * 전체 Namespaces 의 Pods Admin 목록 조회(Get Services Admin list in all namespaces)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the pods all list
     */
    public Object getPodsListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        PodsListAdmin podsListAdminList = commonService.setResultObject(responseMap, PodsListAdmin.class);
        podsListAdminList = commonService.resourceListProcessing(podsListAdminList, offset, limit, orderBy, order, searchName, PodsListAdmin.class);
        podsListAdminList = restartCountProcessing(podsListAdminList);


        return commonService.setResultModel(podsListAdminList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Pods ContainerStatuses 이 없을 경우 처리 (Handle without Pods ContainerStatus)
     *
     * @param podsListAdmin the podsListAdmin
     * @return the pods list
     */
    public PodsListAdmin restartCountProcessing(PodsListAdmin podsListAdmin) {

        for (PodsListAdminList po : podsListAdmin.getItems()) {

            if (po.getStatus().getContainerStatuses() == null) {
                List<ContainerStatusesItem> list = new ArrayList<>();
                ContainerStatusesItem item = new ContainerStatusesItem();
                item.setRestartCount(0);

                list.add(item);

                po.getStatus().setContainerStatuses(list);
            }
        }

        return podsListAdmin;
    }


    /**
     * 참조 리소스 UID 를 통한 Pod List 필터 처리  (Filters with Reference Resource UID)
     *
     * @param podsListAdmin      the podsListAdmin
     * @param ownerReferencesUid the ownerReferencesUid
     * @return the pods list
     */
    public PodsListAdmin podsFIlterWithOwnerReferences(PodsListAdmin podsListAdmin, String ownerReferencesUid) {

        List<PodsListAdminList> podsItem;

        podsItem = podsListAdmin.getItems().stream().filter(x -> x.getMetadata().getOwnerReferences().get(0).getUid().matches(ownerReferencesUid)).collect(Collectors.toList());
        podsListAdmin.setItems(podsItem);

        return podsListAdmin;
    }


}
