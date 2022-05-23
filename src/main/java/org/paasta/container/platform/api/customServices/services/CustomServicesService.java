package org.paasta.container.platform.api.customServices.services;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CustomServices Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.10
 */
@Service
public class CustomServicesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new CustomServices service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public CustomServicesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * Services 목록 조회(Get Services list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the services list
     */
    public CustomServicesList getCustomServicesList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesListUrl()
                        .replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        CustomServicesList customServicesList = commonService.setResultObject(responseMap, CustomServicesList.class);
        customServicesList = commonService.resourceListProcessing(customServicesList, offset, limit, orderBy, order, searchName, CustomServicesList.class);

        return (CustomServicesList) commonService.setResultModel(customServicesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services 상세 조회(Get Services detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the services detail
     */
    public CustomServices getCustomServices(String namespace, String resourceName) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName)
                , HttpMethod.GET, null, Map.class);

        return (CustomServices) commonService.setResultModel(commonService.setResultObject(responseMap, CustomServices.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services YAML 조회(Get Services yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the services yaml
     */
    public Object getCustomServicesYaml(String namespace, String resourceName, HashMap resultMap) {
        String resultString = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services Admin YAML 조회(Get Services Admin yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the services yaml
     */
    public Object getCustomServicesAdminYaml(String namespace, String resourceName, HashMap resultMap) {

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services 생성(Create Services)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public Object createServices(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_SERVICES);
    }


    /**
     * Services 삭제(Delete Services)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    public ResultStatus deleteServices(String namespace, String resourceName, boolean isAdmin) {
        ResultStatus resultStatus;
        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListServicesDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListServicesDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_SERVICES);
    }


    /**
     * Services 수정(Update Services)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public ResultStatus updateServices(String namespace, String resourceName, String yaml, boolean isAdmin) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.PUT, yaml, ResultStatus.class, isAdmin);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_SERVICES_DETAIL.replace("{serviceName:.+}", resourceName));
    }


    /**
     * Services Admin 목록 조회(Get Services Admin list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the services admin list
     */
    public Object getCustomServicesListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesListUrl().replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        CustomServicesListAdmin customServicesListAdmin = commonService.setResultObject(responseMap, CustomServicesListAdmin.class);
        customServicesListAdmin = commonService.resourceListProcessing(customServicesListAdmin, offset, limit, orderBy, order, searchName, CustomServicesListAdmin.class);

        return commonService.setResultModel(customServicesListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services Admin 상세 조회(Get Services Admin detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the services admin
     */
    public Object getCustomServicesAdmin(String namespace, String resourceName) {

        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }
        CustomServicesAdmin customServicesAdmin = commonService.setResultObject(responseMap, CustomServicesAdmin.class);
        customServicesAdmin = commonService.annotationsProcessing(customServicesAdmin, CustomServicesAdmin.class);
        return commonService.setResultModel(customServicesAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 전체 Namespaces 의 Services Admin 목록 조회(Get Services Admin list in all namespaces)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the services admin list
     */
    public Object getCustomServicesListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        CustomServicesListAdmin customServicesListAdmin = commonService.setResultObject(responseMap, CustomServicesListAdmin.class);
        customServicesListAdmin = commonService.resourceListProcessing(customServicesListAdmin, offset, limit, orderBy, order, searchName, CustomServicesListAdmin.class);

        return commonService.setResultModel(customServicesListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

}
