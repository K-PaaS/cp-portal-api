package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasConvertStatus;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatusItem;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.CHECK_N;
import static org.paasta.container.platform.api.common.Constants.CHECK_Y;

/**
 * ResourceQuotas Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 **/
@Service
public class ResourceQuotasService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;


    /**
     * Instantiates a new ResourceQuotas service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public ResourceQuotasService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * ResourceQuotas 목록 조회(Get ResourceQuotas list)
     *
     * @param params the params
     * @return the resourceQuotas list
     */
    public ResourceQuotasList getResourceQuotasList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasListUrl(), HttpMethod.GET, null, Map.class, params);

        ResourceQuotasList resourceQuotasList = commonService.setResultObject(responseMap, ResourceQuotasList.class);
        resourceQuotasList = commonService.resourceListProcessing(resourceQuotasList, params, ResourceQuotasList.class);

        //convert Status
        for (ResourceQuotasListItem rq : resourceQuotasList.getItems()) {

            Map<String, String> hards = rq.getStatus().getHard();
            Map<String, String> useds = rq.getStatus().getUsed();

            if (hards == null || useds == null) {
                Map<String, Object> convertStatusMap = new HashMap<>();
                rq.setConvertStatus(convertStatusMap);
            } else {
                HashMap<String, Object> convertStatus = new HashMap<>();

                for (String key : hards.keySet()) {
                    ResourceQuotasConvertStatus resourceQuotasConvertStatus = new ResourceQuotasConvertStatus();
                    resourceQuotasConvertStatus.setHard(hards.get(key));
                    resourceQuotasConvertStatus.setUsed(useds.get(key));

                    convertStatus.put(key, resourceQuotasConvertStatus);
                }

                Map<String, Object> convertStatusMap = new TreeMap<>(convertStatus);
                rq.setConvertStatus(convertStatusMap);
            }

        }

        return (ResourceQuotasList) commonService.setResultModel(resourceQuotasList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas 상세 조회(Get ResourceQuotas detail)
     *
     * @param params the params
     * @return the resourceQuotas
     */
    public ResourceQuotas getResourceQuotas(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasGetUrl(), HttpMethod.GET, null, Map.class, params);

        ResourceQuotas resourceQuotas = commonService.setResultObject(responseMap, ResourceQuotas.class);

        // resource, hards, useds set
        Map<String, String> hards = resourceQuotas.getStatus().getHard();
        Map<String, String> useds = resourceQuotas.getStatus().getUsed();

        List<ResourceQuotasStatusItem> items = new ArrayList<>();

        //if status (hard, used) is null
        if (hards == null || useds == null) {
            ResourceQuotasStatusItem resourceQuotasStatusItem = new ResourceQuotasStatusItem(Constants.NULL_REPLACE_TEXT, Constants.NULL_REPLACE_TEXT, Constants.NULL_REPLACE_TEXT);
            items.add(resourceQuotasStatusItem);
        } else {
            for (String key : hards.keySet()) {
                ResourceQuotasStatusItem resourceQuotasStatusItem = new ResourceQuotasStatusItem(key, hards.get(key), useds.get(key));
                items.add(resourceQuotasStatusItem);
            }

        }
        resourceQuotas.setItems(items);

        return (ResourceQuotas) commonService.setResultModel(resourceQuotas, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas YAML 조회(Get ResourceQuotas yaml)
     *
     * @param params the params
     * @return the resourceQuotas yaml
     */
    public CommonResourcesYaml getResourceQuotasYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas 생성(Create ResourceQuotas)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createResourceQuotas(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ResourceQuotas 삭제(Delete ResourceQuotas)
     *
     * @param params the params
     * @return the return is succeeded
     */
    public ResultStatus deleteResourceQuotas(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas 수정(Update ResourceQuotas)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus updateResourceQuotas(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas Default Template 목록 조회(Get ResourceQuotas Default Template list)
     *
     * @param params the params
     * @return the resourceQuotas list
     * @throws JsonProcessingException
     */
    public Object getResourceQuotasTemplateList(Params params) throws JsonProcessingException {
        ResourceQuotasList resourceQuotasList = getResourceQuotasList(params);
        ResourceQuotasDefaultList resourceQuotasDefaultList = getResourceQuotasDefaultTemplateList(params);

        ResourceQuotasDefaultList defaultList = new ResourceQuotasDefaultList();
        ResourceQuotasDefault quotasDefault;
        List<ResourceQuotasDefault> quotasDefaultList = new ArrayList<>();

        List<String> k8sRqNameList = resourceQuotasList.getItems().stream().map(ResourceQuotasListItem::getName).collect(Collectors.toList());
//        List<String> dbRqNameList = resourceQuotasDefaultList.getItems().stream().map(ResourceQuotasDefault::getName).collect(Collectors.toList());

        for (ResourceQuotasDefault resourceQuotasDefault : resourceQuotasDefaultList.getItems()) {
            if (!k8sRqNameList.contains(resourceQuotasDefault.getName())) {
                CommonMetaData metadata = new CommonMetaData();

                metadata.setName(resourceQuotasDefault.getName());
                metadata.setCreationTimestamp(resourceQuotasDefault.getCreationTimestamp());

                resourceQuotasDefault.setCheckYn(CHECK_N);
                resourceQuotasDefault.setMetadata(metadata);
                quotasDefaultList.add(resourceQuotasDefault);
            }
        }

        if (resourceQuotasList.getItems().size() > 0) {
            for (ResourceQuotasListItem i : resourceQuotasList.getItems()) {
                ObjectMapper mapper = new ObjectMapper();
                String status = mapper.writeValueAsString(i.getConvertStatus());

                quotasDefault = new ResourceQuotasDefault(i.getName(), status, CHECK_Y, i.getMetadata(), i.getCreationTimestamp());
                quotasDefaultList.add(quotasDefault);

            }
        }

        defaultList.setItems(quotasDefaultList);
        defaultList = commonService.setResultObject(defaultList, ResourceQuotasDefaultList.class);
        defaultList = commonService.resourceListProcessing(defaultList, params, ResourceQuotasDefaultList.class);

        return commonService.setResultModel(defaultList, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * 네임스페이스 생성 시 ResourceQuotas Default Template 목록 조회(Get ResourceQuotas Default Template list for Creating Namespace)
     *
     * @param params the params
     * @return the resourceQuotas list
     * @throws JsonProcessingException
     */
    public ResourceQuotasDefaultList getResourceQuotasDefaultTemplateList(Params params) {
        return restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class, params);
    }


}