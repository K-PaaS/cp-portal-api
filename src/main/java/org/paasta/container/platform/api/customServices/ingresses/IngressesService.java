package org.paasta.container.platform.api.customServices.ingresses;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.customServices.services.CustomServices;
import org.paasta.container.platform.api.customServices.services.CustomServicesAdmin;
import org.paasta.container.platform.api.customServices.services.CustomServicesList;
import org.paasta.container.platform.api.customServices.services.CustomServicesListAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Ingresses Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.17
 */
@Service
public class IngressesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Ingresses service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public IngressesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Ingresses 목록 조회(Get Ingresses list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the ingresses list
     */
    public Object getIngressesList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesListUrl()
                        .replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        CustomServicesList customServicesList = commonService.setResultObject(responseMap, CustomServicesList.class);
        customServicesList = commonService.resourceListProcessing(customServicesList, offset, limit, orderBy, order, searchName, CustomServicesList.class);

        return (CustomServicesList) commonService.setResultModel(customServicesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses 상세 조회(Get Ingresses detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the ingresses detail
     */
    public Object getIngresses(String namespace, String resourceName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName)
                , HttpMethod.GET, null, Map.class);

        return (CustomServices) commonService.setResultModel(commonService.setResultObject(responseMap, CustomServices.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses YAML 조회(Get Ingresses yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the ingresses yaml
     */
    public Object getIngressesYaml(String namespace, String resourceName, HashMap<Object, Object> resultMap) {

        String resultString = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", resourceName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses Admin YAML 조회(Get Ingresses Admin yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the ingresses yaml
     */
    public Object getIngressesAdminYaml(String namespace, String resourceName, HashMap<Object, Object> resultMap) {
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl()
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
     * Ingresses 생성(Create Ingresses)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public Object createIngresses(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_INGRESSES);
    }

    /**
     * Ingresses 삭제(Delete Ingresses)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    public ResultStatus deleteIngresses(String namespace, String resourceName, boolean isAdmin) {
        ResultStatus resultStatus;
        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListIngressesDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListIngressesDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_INGRESSES);
    }

    /**
     * Ingresses 수정(Update Ingresses)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public ResultStatus updateIngresses(String namespace, String resourceName, String yaml, boolean isAdmin) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.PUT, yaml, ResultStatus.class, isAdmin);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_INGRESSES_DETAIL.replace("{serviceName:.+}", resourceName));
    }

    /**
     * Ingresses Admin 목록 조회(Get Ingresses Admin list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the services admin list
     */
    public Object getIngressesListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesListUrl().replace("{namespace}", namespace)
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
     * Ingresses Admin 상세 조회(Get Ingresses Admin detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the ingresses admin
     */
    public Object getIngressesAdmin(String namespace, String resourceName) {

        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl()
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
     * 전체 Namespaces 의 Ingresses Admin 목록 조회(Get Ingresses Admin list in all namespaces)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the ingresses admin list
     */
    public Object getIngressesListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
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














