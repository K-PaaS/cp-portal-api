package org.paasta.container.platform.api.roles;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Roles Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class RolesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResultStatusService resultStatusService;


    /**
     * Instantiates a new Roles service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public RolesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Roles 목록 조회(Get Roles list)
     *
     * @param params the params
     * @return the roles list
     */
    public RolesList getRolesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesListUrl(), HttpMethod.GET, null, Map.class, params);
        RolesList rolesList = commonService.setResultObject(responseMap, RolesList.class);
        rolesList = commonService.resourceListProcessing(rolesList, params, RolesList.class);
        return (RolesList) commonService.setResultModel(rolesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles 상세 조회(Get Roles detail)
     *
     * @param params the params
     * @return the roles
     */
    public Roles getRoles(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesGetUrl(), HttpMethod.GET, null, Map.class, params);
        Roles roles = commonService.setResultObject(responseMap, Roles.class);
        roles = commonService.annotationsProcessing(roles, Roles.class);
        return (Roles) commonService.setResultModel(roles, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles YAML 조회(Get Roles yaml)
     *
     * @param params the params
     * @return the roles yaml
     */
    public CommonResourcesYaml getRolesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles 생성(Create Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createRoles(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles 삭제(Delete Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deleteRoles(Params params) {
        if(propertyService.getRolesList().contains(params.getResourceName())) {
            return resultStatusService.DO_NOT_DELETE_DEFAULT_RESOURCES();
        }

        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Roles 수정(Update Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus updateRoles(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * User 가 속해 있는 Namespace 와 Role 목록 조회(Get Namespace and Roles List to which User belongs)
     *
     * @param params the params
     * @return return is succeeded
     */
    public Object getNamespacesRolesTemplateList(Params params) {
        params.setNamespace(Constants.ALL_NAMESPACES);
        RolesList rolesLIst = getRolesList(params);

        if (!params.getUserId().equals(Constants.ALL_USER_ID)) {
            UsersList usersList = restTemplateService.send(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_USER_AUTH_ID
                    .replace("{cluster:.+}", params.getCluster())
                    .replace("{userAuthId:.+}", params.getUserAuthId()), HttpMethod.GET, null, UsersList.class, params);

            for (RolesListItem item : rolesLIst.getItems()) {
                for (Users user : usersList.getItems()) {
                    if (user.getCpNamespace().equals(item.getNamespace()) && user.getRoleSetCode().equals(item.getName())) {
                        item.setCheckYn(Constants.CHECK_Y);
                        item.setUserType(user.getUserType());
                        if(Constants.AUTH_NAMESPACE_ADMIN.equals(user.getUserType())) {
                            item.setIsNamespaceAdminRole(Constants.CHECK_Y);
                        }
                    }
                }
            }
        }

        rolesLIst = commonService.resourceListProcessing(rolesLIst, params, RolesList.class);
        return commonService.setResultModel(rolesLIst, Constants.RESULT_STATUS_SUCCESS);
    }
}