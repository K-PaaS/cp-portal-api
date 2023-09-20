package org.container.platform.api.clusters.limitRanges;

import com.google.gson.internal.LinkedTreeMap;
import org.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.container.platform.api.common.Constants.CHECK_N;
import static org.container.platform.api.common.Constants.CHECK_Y;

/**
 * LimitRanges Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class LimitRangesService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new LimitRanges service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public LimitRangesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * LimitRanges 목록 조회(Get LimitRanges list)
     *
     * @param params the params
     * @return the limitRanges list
     */
    public LimitRangesList getLimitRangesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListUrl(), HttpMethod.GET, null, Map.class, params);
        LimitRangesList limitRangesList = commonService.setResultObject(responseMap, LimitRangesList.class);
        limitRangesList = commonService.resourceListProcessing(limitRangesList, params, LimitRangesList.class);
        return (LimitRangesList) commonService.setResultModel(limitRangesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**

    /**
     * 전체 Namespaces 의 LimitRanges 목록 조회(Get LimitRanges list in all namespaces)
     *
     * @param params the params
     * @return the limitRanges list
     */
    public LimitRangesList getLimitRangesListAllNamespaces(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListAllNamespacesUrl()
//                        + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class, params);
        LimitRangesList limitRangesList = commonService.setResultObject(responseMap, LimitRangesList.class);
        limitRangesList = commonService.resourceListProcessing(limitRangesList, params, LimitRangesList.class);
        return (LimitRangesList) commonService.setResultModel(limitRangesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * LimitRanges 상세 조회(Get LimitRanges detail)
     *
     * @param params the params
     * @return the limitRanges detail
     */
    public LimitRanges getLimitRanges(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, Map.class, params);

        LimitRanges limitRanges = commonService.setResultObject(responseMap, LimitRanges.class);
        List<LimitRangesItem> limits = limitRanges.getSpec().getLimits();
        List<LimitRangesItem> serversItemList = new ArrayList<>();


        for (LimitRangesItem item:limits) {
            List<String> typeList = Constants.LIMIT_RANGE_TYPE_LIST;

            for (String type : typeList) {
                if(type.equals(item.getType())) {
                    if(Constants.LIMIT_RANGE_TYPE_CONTAINER.equals(type) || Constants.LIMIT_RANGE_TYPE_POD.equals(type)) {
                        for (String resourceType : Constants.SUPPORTED_RESOURCE_LIST) {
                            LimitRangesItem serversItem = new LimitRangesItem();
                            serversItem = (LimitRangesItem) getLimitRangesTemplateItem(limitRanges.getName(), limitRanges.getCreationTimestamp(), type, resourceType, item, serversItem);

                            if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                                serversItemList.add(serversItem);
                            }
                        }
                    } else {
                        String resourceType = Constants.SUPPORTED_RESOURCE_STORAGE;
                        LimitRangesItem serversItem = new LimitRangesItem();
                        serversItem = (LimitRangesItem) getLimitRangesTemplateItem(limitRanges.getName(), limitRanges.getCreationTimestamp(), type, resourceType, item, serversItem);

                        if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                            serversItemList.add(serversItem);
                        }
                    }
                }
            }
        }

        limitRanges.setItems(serversItemList);

        return (LimitRanges) commonService.setResultModel(commonService.setResultObject(limitRanges, LimitRanges.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * LimitRanges YAML 조회(Get LimitRanges yaml)
     *
     * @param params the params
     * @return the limitRanges yaml
     */
    public Object getLimitRangesYaml(Params params, HashMap resultMap) {

        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);

        if (CommonUtils.isResultStatusInstanceCheck(resourceYaml)) {
            return resourceYaml;
        }
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resourceYaml);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * LimitRanges 생성(Create LimitRanges)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createLimitRanges(Params params) {

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * LimitRanges 삭제(Delete LimitRanges)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteLimitRanges(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * LimitRanges 수정(Update LimitRanges)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus updateLimitRanges(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 네임스페이스 생성 시 Defult LimitRanges Template 목록 조회(Get Default LimitRanges Template list for Creating Namespace)
     *
     * @param params the params
     * @return the limitRanges template list
     */
    public LimitRangesDefaultList getLimitRangesDefaultTemplateList(Params params) {
        return restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class, params);
    }

    /**
     * LimitRanges Template 목록 조회(Get LimitRanges Template list)
     *
     * @param params the params
     * @return the limitRanges template list
     */
    public Object getLimitRangesTemplateList(Params params) {
        LimitRangesList limitRangesList = getLimitRangesList(params);
        LimitRangesDefaultList defaultList = getLimitRangesDefaultTemplateList(params);

        List<LimitRangesListItem> adminItems = limitRangesList.getItems();
        List<LimitRangesTemplateItem> serversItemList = new ArrayList();

        LimitRangesTemplateList serverList = new LimitRangesTemplateList();

        List<String> k8sLrNameList = limitRangesList.getItems().stream().map(LimitRangesListItem::getName).collect(Collectors.toList());

        for (LimitRangesDefault limitRangesDefault : defaultList.getItems()) {

            if (!k8sLrNameList.contains(limitRangesDefault.getName())) {
                serversItemList.add(getLimitRangesDb(limitRangesDefault, CHECK_N));
            }

        }

            for (LimitRangesListItem i : adminItems) {

                    for (LimitRangesItem item : i.getSpec().getLimits()) {
                        List<String> typeList = Constants.LIMIT_RANGE_TYPE_LIST;

                        for (String type : typeList) {
                            if(type.equals(item.getType())) {
                                if(Constants.LIMIT_RANGE_TYPE_CONTAINER.equals(type) || Constants.LIMIT_RANGE_TYPE_POD.equals(type)) {
                                    for (String resourceType : Constants.SUPPORTED_RESOURCE_LIST) {
                                        LimitRangesTemplateItem serversItem = new LimitRangesTemplateItem();
                                        serversItem = (LimitRangesTemplateItem) getLimitRangesTemplateItem(i.getName(), i.getCreationTimestamp(), type, resourceType, item, serversItem);

                                        if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                                            serversItemList.add(serversItem);
                                        }
                                    }
                                } else {
                                    String resourceType = Constants.SUPPORTED_RESOURCE_STORAGE;
                                    LimitRangesTemplateItem serversItem = new LimitRangesTemplateItem();
                                    serversItem = (LimitRangesTemplateItem) getLimitRangesTemplateItem(i.getName(), i.getCreationTimestamp(), type, resourceType, item, serversItem);

                                    if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                                        serversItemList.add(serversItem);
                                    }
                                }
                            }
                        }
                    }

            }


        serverList.setItems(serversItemList);
        serverList = commonService.setResultObject(serverList, LimitRangesTemplateList.class);
        serverList = commonService.resourceListProcessing(serverList, params, LimitRangesTemplateList.class);

        return commonService.setResultModel(serverList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * LimitRanges DB Template 형식 맞춤(Set LimitRanges DB template)
     *
     * @param limitRangesDefault the limitRangesDefault
     * @param yn the yn
     * @return the limitRanges template item
     */
    public LimitRangesTemplateItem getLimitRangesDb(LimitRangesDefault limitRangesDefault, String yn) {
        LimitRangesTemplateItem item = new LimitRangesTemplateItem();
        CommonMetaData metadata = new CommonMetaData();

        item.setName(limitRangesDefault.getName());
        item.setType(limitRangesDefault.getType());
        item.setResource(limitRangesDefault.getResource());
        item.setMin(limitRangesDefault.getMin());
        item.setMax(limitRangesDefault.getMax());
        item.setDefaultRequest(limitRangesDefault.getDefaultRequest());
        item.setDefaultLimit(limitRangesDefault.getDefaultLimit());
        item.setCheckYn(yn);
        metadata.setCreationTimestamp(limitRangesDefault.getCreationTimestamp());
        item.setMetadata(metadata);
        item.setCreationTimestamp(limitRangesDefault.getCreationTimestamp());
        return item;
    }


    /**
     * 존재하는 각 Type 과 Resource Type 별로 LimitRanges 자원 설정 값 셋팅(Set LimitRanges resources value for type)
     *
     * @param name              the name
     * @param creationTimestamp the creationTimestamp
     * @param type              the type
     * @param resourceType      the resource type
     * @param item              the item
     * @param object            the object
     @return the limitRangesTemplateItem
     */
    public Object getLimitRangesTemplateItem(String name, String creationTimestamp, String type, String resourceType, LimitRangesItem item, Object object) {
        LinkedTreeMap<String, String> defaultLimit = null;
        LinkedTreeMap<String, String> defaultRequest = null;
        LinkedTreeMap<String, String> max = null;
        LinkedTreeMap<String, String> min = null;

        if(!item.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT)) {
            defaultLimit = (LinkedTreeMap) item.getDefaultLimit();
        }

        if(!item.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT)) {
            defaultRequest = (LinkedTreeMap) item.getDefaultRequest();
        }

        if(!item.getMax().equals(Constants.NULL_REPLACE_TEXT)) {
            max = (LinkedTreeMap) item.getMax();
        }

        if(!item.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
            min = (LinkedTreeMap) item.getMin();
        }

        if(object instanceof LimitRangesTemplateItem) {
            LimitRangesTemplateItem serversItem = new LimitRangesTemplateItem();
            CommonMetaData metadata = new CommonMetaData();
            metadata.setCreationTimestamp(creationTimestamp);
            serversItem.setName(name);
            serversItem.setType(type);
            serversItem.setResource(resourceType);
            serversItem.setCheckYn(CHECK_Y);
            serversItem.setCreationTimestamp(creationTimestamp);
            serversItem.setMetadata(metadata);

            serversItem = (LimitRangesTemplateItem) commonSetResourceValue(resourceType, defaultLimit, defaultRequest, max, min, serversItem);

            return serversItem;

        } else {
            LimitRangesItem rangesItem = new LimitRangesItem();
            rangesItem.setType(type);
            rangesItem.setResource(resourceType);

            rangesItem = (LimitRangesItem) commonSetResourceValue(resourceType, defaultLimit, defaultRequest, max, min, rangesItem);

            return rangesItem;
        }
    }


    /**
     * Common Resource 값 세팅(Set Common Resource value)
     *
     * @param resourceType      the resource type
     * @param defaultLimit      the default limit
     * @param defaultRequest    the default request
     * @param max               the max
     * @param min               the min
     * @param item              the item
     * @return the object
     */
    public Object commonSetResourceValue(String resourceType, LinkedTreeMap<String, String> defaultLimit, LinkedTreeMap<String, String> defaultRequest, LinkedTreeMap<String, String> max, LinkedTreeMap<String, String> min, Object item) {
        if(item instanceof LimitRangesTemplateItem) {
            LimitRangesTemplateItem serversItem = (LimitRangesTemplateItem) item;

            if(defaultLimit != null) {
                for (String mapKey : defaultLimit.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setDefaultLimit(defaultLimit.get(mapKey));
                        break;
                    } else {
                        serversItem.setDefaultLimit(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setDefaultLimit(Constants.NULL_REPLACE_TEXT);
            }

            if(defaultRequest != null) {
                for (String mapKey : defaultRequest.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setDefaultRequest(defaultRequest.get(mapKey));
                        break;
                    } else {
                        serversItem.setDefaultRequest(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setDefaultRequest(Constants.NULL_REPLACE_TEXT);
            }

            if(min != null) {
                for (String mapKey : min.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setMin(min.get(mapKey));
                        break;
                    } else {
                        serversItem.setMin(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setMin(Constants.NULL_REPLACE_TEXT);
            }

            if(max != null) {
                for (String mapKey : max.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setMax(max.get(mapKey));
                        break;
                    } else {
                        serversItem.setMax(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setMax(Constants.NULL_REPLACE_TEXT);
            }

            return serversItem;
        } else {
            LimitRangesItem serversItem = (LimitRangesItem) item;

            if(defaultLimit != null) {
                for (String mapKey : defaultLimit.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setDefaultLimit(defaultLimit.get(mapKey));
                        break;
                    } else {
                        serversItem.setDefaultLimit(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setDefaultLimit(Constants.NULL_REPLACE_TEXT);
            }

            if(defaultRequest != null) {
                for (String mapKey : defaultRequest.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setDefaultRequest(defaultRequest.get(mapKey));
                        break;
                    } else {
                        serversItem.setDefaultRequest(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setDefaultRequest(Constants.NULL_REPLACE_TEXT);
            }

            if(min != null) {
                for (String mapKey : min.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setMin(min.get(mapKey));
                        break;
                    } else {
                        serversItem.setMin(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setMin(Constants.NULL_REPLACE_TEXT);
            }

            if(max != null) {
                for (String mapKey : max.keySet()) {
                    if(resourceType.equals(mapKey)) {
                        serversItem.setMax(max.get(mapKey));
                        break;
                    } else {
                        serversItem.setMax(Constants.NULL_REPLACE_TEXT);
                    }
                }
            } else {
                serversItem.setMax(Constants.NULL_REPLACE_TEXT);
            }

            return serversItem;
        }

    }

}
