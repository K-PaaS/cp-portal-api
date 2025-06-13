package org.container.platform.api.workloads.pods;


import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.YamlUtil;
import org.container.platform.api.metrics.MetricsService;
import org.container.platform.api.workloads.pods.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.*;
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
    private final MetricsService metricsService;

    /**
     * Instantiates a new Pods service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public PodsService(RestTemplateService restTemplateService, CommonService commonService,
                       PropertyService propertyService, MetricsService metricsService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.metricsService = metricsService;
    }


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

        if(params.includeUsage) {
            params.setIsClusterToken(true);
            podsList = metricsService.setPodsMetrics(podsList, params);
        }
        return (PodsList) commonService.setResultModel(podsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Selector 값에 따른 Pods 목록 조회(Get Pods By Selector)
     *
     * @param params the params
     * @return the pods list
     */
    public PodsList getPodListWithLabelSelector(Params params) {
        String labelSelector = "?labelSelector=" + params.getSelector();

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListUrl() + labelSelector, HttpMethod.GET, null, Map.class, params);

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
        String fieldSelector = "?fieldSelector=spec.nodeName=" + params.getNodeName();
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsListAllNamespacesUrl() + fieldSelector
                , HttpMethod.GET, null, Map.class, params);

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
        YamlUtil.allowTrafficPodLabels(params, true);
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


    /**
     * Namespaces의 Labels 목록 조회(Get Labels List By Namespaces)
     *
     * @param params the params
     * @return the pods list
     */
    public PodsLabels getLabelsList(Params params) {
        PodsList podsList = this.getPodsList(params);
        Map<String, Set<Object>> labels = new HashMap<>();

        for(PodsListItem podsListItem : podsList.getItems()) {
            if(podsListItem.getLabels() == "-"){
                continue;
            }
            Map<String, Object> itemLabels = (Map<String, Object>) podsListItem.getLabels();
            for (Map.Entry<String, Object> entry : itemLabels.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                labels.computeIfAbsent(key, k -> new HashSet<>()).add(value);
            }
        }

        PodsLabels podsLabels = new PodsLabels();
        podsLabels.setItems(Collections.singletonList(new PodsLabelsItem(labels)));

        return (PodsLabels) commonService.setResultModel(podsLabels, Constants.RESULT_STATUS_SUCCESS);
    }

}
