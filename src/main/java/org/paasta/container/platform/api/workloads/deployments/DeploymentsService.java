package org.paasta.container.platform.api.workloads.deployments;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Deployments Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.08
 */
@Service
public class DeploymentsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Deployments service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public DeploymentsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Deployments 목록 조회(Get Deployments list)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the deployments list
     */
    public DeploymentsList getDeploymentsList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListUrl()
                        .replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        DeploymentsList deploymentsList = commonService.setResultObject(responseMap, DeploymentsList.class);
        deploymentsList = commonService.resourceListProcessing(deploymentsList, offset, limit, orderBy, order, searchName, DeploymentsList.class);
        return (DeploymentsList) commonService.setResultModel(deploymentsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Deployments 목록 조회(Get Deployments list)
     * (Admin Portal)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the deployments list
     */
    public Object getDeploymentsListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        DeploymentsListAdmin deploymentsListAdmin = commonService.setResultObject(responseMap, DeploymentsListAdmin.class);
        deploymentsListAdmin = commonService.resourceListProcessing(deploymentsListAdmin, offset, limit, orderBy, order, searchName, DeploymentsListAdmin.class);

        return commonService.setResultModel(deploymentsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Deployments 상세 조회(Get Deployments detail)
     * (User Portal)
     *
     * @param namespace      the namespace
     * @param deploymentName the deployments name
     * @return the deployments detail
     */
    public Deployments getDeployments(String namespace, String deploymentName) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", deploymentName)
                , HttpMethod.GET, null, Map.class);


        return (Deployments) commonService.setResultModel(commonService.setResultObject(responseMap, Deployments.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments 상세 조회(Get Deployments detail)
     * (Admin Portal)
     *
     * @param namespace      the namespace
     * @param deploymentName the deployments name
     * @return the deployments detail
     */
    public Object getDeploymentsAdmin(String namespace, String deploymentName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", deploymentName)
                , HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }

        DeploymentsAdmin deploymentsAdmin = commonService.setResultObject(responseMap, DeploymentsAdmin.class);
        deploymentsAdmin = commonService.annotationsProcessing(deploymentsAdmin, DeploymentsAdmin.class);

        return commonService.setResultModel(deploymentsAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments YAML 조회(Get Deployments yaml)
     *
     * @param namespace      the namespace
     * @param deploymentName the deployments name
     * @param resultMap      the result map
     * @return the deployments yaml
     */
    public Object getDeploymentsYaml(String namespace, String deploymentName, HashMap resultMap) {
        String resultString = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", deploymentName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        //noinspection unchecked
        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments Admin YAML 조회(Get Deployments Admin yaml)
     *
     * @param namespace      the namespace
     * @param deploymentName the deployments name
     * @param resultMap      the result map
     * @return the deployments yaml
     */
    public Object getDeploymentsAdminYaml(String namespace, String deploymentName, HashMap resultMap) {

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", deploymentName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments 생성(Create Deployments)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    public Object createDeployments(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS);
    }

    /**
     * Deployments 삭제(Delete Deployments)
     *
     * @param namespace the namespace
     * @param name      the deployments name
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    public ResultStatus deleteDeployments(String namespace, String name, boolean isAdmin) {
        ResultStatus resultStatus;

        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListDeploymentsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS);
    }


    /**
     * Deployments 수정(Update Deployments)
     *
     * @param namespace the namespace
     * @param name      the deployments name
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    public ResultStatus updateDeployments(String namespace, String name, String yaml, boolean isAdmin) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.PUT, yaml, ResultStatus.class, isAdmin);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS_DETAIL.replace("{deploymentName:.+}", name));
    }


    /**
     * 전체 Namespaces 의 Deployments Admin 목록 조회(Get Deployments Admin list in all namespaces)
     *
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the deployments all list
     */
    public Object getDeploymentsListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        DeploymentsListAdmin deploymentsListAdmin = commonService.setResultObject(responseMap, DeploymentsListAdmin.class);
        deploymentsListAdmin = commonService.resourceListProcessing(deploymentsListAdmin, offset, limit, orderBy, order, searchName, DeploymentsListAdmin.class);

        return commonService.setResultModel(deploymentsListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }
}
