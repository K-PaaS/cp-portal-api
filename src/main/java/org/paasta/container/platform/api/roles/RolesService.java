package org.paasta.container.platform.api.roles;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.signUp.SignUpAdminService;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.paasta.container.platform.api.common.Constants.URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_NAME_USER_ID;

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
    private final SignUpAdminService signUpAdminService;
    private final ResultStatusService resultStatusService;
    /**
     * Instantiates a new Roles service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public RolesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, SignUpAdminService signUpAdminService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.signUpAdminService = signUpAdminService;
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
     * 전체 Namespaces 의 Roles 목록 조회(Get Roles list in all namespaces)
     *
     * @param params the params
     * @return the roles list
     */
    public RolesList getRolesListAllNamespaces(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesListAllNamespacesUrl(),
                HttpMethod.GET, null, Map.class, params);
        RolesList rolesList = commonService.setResultObject(responseMap, RolesList.class);
        List<RolesListItem> rolesListItems = new ArrayList<>();

        for (RolesListItem item : rolesList.getItems()) {
            if (!propertyService.getDefaultNamespace().equals(item.getNamespace()) && !item.getNamespace().startsWith("kube") && !item.getNamespace().equals("default")) {
                rolesListItems.add(item);
            }
        }

        rolesList.setItems(rolesListItems);

        rolesList = commonService.resourceListProcessing(rolesList, params, RolesList.class);
        return (RolesList) commonService.setResultModel(rolesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * User 가 속해 있는 Namespace 와 Role 목록 조회(Get Namespace and Roles List to which User belongs)
     *
     * @param params the params
     * @return return is succeeded
     */
    public Object getNamespacesRolesTemplateList(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesListAllNamespacesUrl(), HttpMethod.GET, null, Map.class, params);


        RolesListAllNamespaces rolesListAllNamespaces = commonService.setResultObject(responseMap, RolesListAllNamespaces.class);

        List<RolesListAllNamespaces.RolesListAllNamespacesItem> rolesListAdminItems = new ArrayList<>();

        for (RolesListAllNamespaces.RolesListAllNamespacesItem item : rolesListAllNamespaces.getItems()) {
            if (!propertyService.getIgnoreNamespaceList().contains(item.getNamespace())) {
                rolesListAdminItems.add(item);
            }
        }

        rolesListAllNamespaces.setItems(rolesListAdminItems);

        if (params.getUserId().equals(Constants.ALL_USER_ID)) {
            for (RolesListAllNamespaces.RolesListAllNamespacesItem item : rolesListAllNamespaces.getItems()) {
                item.setCheckYn(Constants.CHECK_N);
                item.setUserType(Constants.NOT_ASSIGNED_ROLE);
            }
        } else {
            UsersList usersList = restTemplateService.send(Constants.TARGET_COMMON_API, URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_NAME_USER_ID
                    .replace("{cluster:.+}", params.getCluster())
                    .replace("{userId:.+}", params.getUserId()), HttpMethod.GET, null, UsersList.class, params);

            for (RolesListAllNamespaces.RolesListAllNamespacesItem item : rolesListAllNamespaces.getItems()) {
                item.setCheckYn(Constants.CHECK_N);
                item.setUserType(Constants.NOT_ASSIGNED_ROLE);
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


        rolesListAllNamespaces = commonService.resourceListProcessing(rolesListAllNamespaces, params, RolesListAllNamespaces.class);
        return commonService.setResultModel(rolesListAllNamespaces, Constants.RESULT_STATUS_SUCCESS);
    }
}