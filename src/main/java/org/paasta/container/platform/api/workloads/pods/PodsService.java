package org.paasta.container.platform.api.workloads.pods;


import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
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
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.20
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

      //  getMergeMetric(podsList, podsMetrics);

        return podsList;
    }

/*    *//**
     * Pods Metric 정보 병합(Merge Pods Metric List)
     *
     * @param podsList    the podsList
     * @param podsMetrics the podsMetrics
     *//*
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
    }*/

    /**
     * Pods 목록 조회(Get Pods List)
     *
     * @param params the params
     * @return the pods list
     */
    public PodsList getPodsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl(), HttpMethod.GET, null, Map.class, params);
        PodsList podsList = commonService.setResultObject(responseMap, PodsList.class);
        podsList =  commonService.resourceListProcessing(podsList, params, PodsList.class);
        podsList = restartCountProcessing(podsList);
        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Selector 값에 따른 Pods 목록 조회(Get Pods By Selector)
     *
     * @param params the params
     * @return the pods list
     */
    public PodsList getPodListWithLabelSelector(Params params) {
        params.setAddParam("?labelSelector=" + params.getSelector());
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl() , HttpMethod.GET, null, Map.class, params);

        PodsList podsList = commonService.setResultObject(responseMap, PodsList.class);

        if (params.getType().equals(Constants.REPLICASETS_FOR_SELECTOR)) {
            podsList = podsFIlterWithOwnerReferences(podsList, params.getOwnerReferencesUid());
        }
        podsList = commonService.resourceListProcessing(podsList, params, PodsList.class);
        podsList = restartCountProcessing(podsList);

        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Node 명에 따른 Pods 목록 조회(Get Pods By Node)
     *
     * @param params the params
     * @return the pods list
     */
    public PodsList getPodsListByNode(Params params) {
        params.setAddParam(",spec.nodeName=" + params.getNodeName());
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListAllNamespacesUrl(), HttpMethod.GET, null, Map.class, params);

        PodsList podsList = commonService.setResultObject(responseMap, PodsList.class);
        podsList = commonService.resourceListProcessing(podsList, params, PodsList.class);
        podsList = restartCountProcessing(podsList);
        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Pods 상세 조회(Get Pods Detail)
     *
     * @param params the params
     * @return the pods detail
     */
    public Pods getPods(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);

        PodsStatus status = commonService.setResultObject(responseMap.get(STATUS_FIELD_NAME), PodsStatus.class);

        if(status.getContainerStatuses() == null) {
            List<ContainerStatusesItem> list = new ArrayList<>();
            ContainerStatusesItem item = new ContainerStatusesItem();
            item.setRestartCount(0);

            list.add(item);
            status.setContainerStatuses(list);
        }

        responseMap.put(STATUS_FIELD_NAME, status);

        Pods pods = commonService.setResultObject(responseMap, Pods.class);
        pods = commonService.annotationsProcessing(pods, Pods.class);

        return (Pods) commonService.setResultModel(pods, Constants.RESULT_STATUS_SUCCESS);

    }



    /**
     * Pods YAML 조회(Get Pods Yaml)
     *
     * @param params the params
     * @return the pods yaml
     */
    public CommonResourcesYaml getPodsYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Pods 생성(Create Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createPods(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Pods 수정(Update Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updatePods(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }




    /**
     * Pods 삭제(Delete Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deletePods(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }




    /**
     * Pods ContainerStatuses 이 없을 경우 처리 (Handle without Pods ContainerStatus)
     *
     * @param podsList the pods list
     * @return the pods list
     */
    public PodsList restartCountProcessing(PodsList podsList) {

        for (PodsListItem po : podsList.getItems()) {

            if (po.getStatus().getContainerStatuses() == null) {
                List<ContainerStatusesItem> list = new ArrayList<>();
                ContainerStatusesItem item = new ContainerStatusesItem();
                item.setRestartCount(0);

                list.add(item);

                po.getStatus().setContainerStatuses(list);
            }
        }

        return podsList;
    }


    /**
     * 참조 리소스 UID 를 통한 Pod List 필터 처리  (Filters with Reference Resource UID)
     *
     * @param podsList      the podsList
     * @param ownerReferencesUid the ownerReferencesUid
     * @return the pods list
     */
    public PodsList podsFIlterWithOwnerReferences(PodsList podsList, String ownerReferencesUid) {

        List<PodsListItem> podsItem;

        podsItem = podsList.getItems().stream().filter(x -> x.getMetadata().getOwnerReferences().get(0).getUid().matches(ownerReferencesUid)).collect(Collectors.toList());
        podsList.setItems(podsItem);

        return podsList;
    }


}
