package org.paasta.container.platform.api.clusters.namespaces;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesList;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.paasta.container.platform.api.clusters.namespaces.support.NamespacesListItem;
import org.paasta.container.platform.api.clusters.namespaces.support.NamespacesListSupport;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Namespaces Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class NamespacesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespacesService.class);

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResourceYamlService resourceYamlService;
    private final UsersService usersService;
    private final AccessTokenService accessTokenService;
    private final ResourceQuotasService resourceQuotasService;
    private final LimitRangesService limitRangesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Namespace service
     *
     * @param restTemplateService   the rest template service
     * @param commonService         the common service
     * @param propertyService       the property service
     * @param resourceYamlService   the resource yaml service
     * @param usersService          the users service
     * @param accessTokenService    the access token service
     * @param resourceQuotasService the resource quotas service
     * @param limitRangesService    the limit ranges service
     */
    @Autowired
    public NamespacesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService,
                             ResourceYamlService resourceYamlService, UsersService usersService, AccessTokenService accessTokenService,
                             ResourceQuotasService resourceQuotasService, LimitRangesService limitRangesService, ResultStatusService resultStatusService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resourceYamlService = resourceYamlService;
        this.usersService = usersService;
        this.accessTokenService = accessTokenService;
        this.resourceQuotasService = resourceQuotasService;
        this.limitRangesService = limitRangesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Namespaces 목록 조회(Get Namespaces List)
     *
     * @param params the params
     * @return the namespaces list
     */
    public NamespacesList getNamespacesList(Params params) {
        params.setNamespace(ALL_NAMESPACES);
        params.setSelectorType(RESOURCE_CLUSTER);
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesListUrl(), HttpMethod.GET, null, Map.class, params);
        NamespacesList namespacesList = commonService.setResultObject(responseMap, NamespacesList.class);
        namespacesList = commonService.resourceListProcessing(namespacesList, params, NamespacesList.class);
        return (NamespacesList) commonService.setResultModel(namespacesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespaces 상세 조회(Get Namespaces Detail)
     *
     * @param params the params
     * @return the namespaces detail
     */
    public Namespaces getNamespaces(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, Map.class, params);
        Namespaces namespaces = commonService.setResultObject(responseMap, Namespaces.class);
        namespaces = commonService.annotationsProcessing(namespaces, Namespaces.class);
        return (Namespaces) commonService.setResultModel(namespaces, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespaces Yaml 조회(Get Namespaces Yaml)
     *
     * @param params the params
     * @return the namespaces yaml
     */
    public CommonResourcesYaml getNamespacesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * Namespaces 삭제(Delete Namespaces)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteNamespaces(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

        List<String> userNamesList = usersService.getUsersNameListByNamespace(params.getCluster(), params.getNamespace()).get(USERS);
        for (String userId : userNamesList) {
         //   usersService.deleteUsers(usersService.getUsers(params.getCluster(), params.getNamespace(), userId));
        }
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespaces 생성(Create Namespaces)
     *
     * @param params       the params
     * @param initTemplate the init template
     * @return the resultStatus
     */
    public ResultStatus createInitNamespaces(Params params, NamespacesInitTemplate initTemplate) {
        String namespace = initTemplate.getName();
        String userId = initTemplate.getNsAdminUserId();

        Users newNsUser = null;
        try {
            newNsUser = usersService.getUsers(params.getCluster(), propertyService.getDefaultNamespace(), userId);
        } catch (Exception e) {
            return resultStatusService.UNAPPROACHABLE_USERS();
        }

        String nsAdminUserSA = newNsUser.getServiceAccountName();
        params.setRs_sa(nsAdminUserSA);
        params.setRs_role(propertyService.getAdminRole());
        params.setNamespace(namespace);

        // 1. namespace 생성
        resourceYamlService.createNamespace(params);

        // 2. init-role, admin-role 생성
        resourceYamlService.createInitRole(params);
        resourceYamlService.createAdminRole(params);

        // 3. namespace 관리자 sa 생성
        ResultStatus createSAresult = resourceYamlService.createServiceAccount(params);
        if (createSAresult.getResultCode().equalsIgnoreCase(RESULT_STATUS_FAIL)) {
            resourceYamlService.deleteNamespaceYaml(params);
            return createSAresult;
        }

        // 4. namespace 관리자 rb 생성
        ResultStatus createRBresult = resourceYamlService.createRoleBinding(params);
        if (createRBresult.getResultCode().equalsIgnoreCase(RESULT_STATUS_FAIL)) {
            resourceYamlService.deleteNamespaceYaml(params);
            return createRBresult;
        }

        // 5. namespace 관리자 DB user 생성
        String saSecretName = resourceYamlService.getSecretName(params);

        newNsUser.setId(0);
        newNsUser.setCpNamespace(namespace);
        newNsUser.setRoleSetCode(propertyService.getAdminRole());
        newNsUser.setSaSecret(saSecretName);
        newNsUser.setSaToken(accessTokenService.getSecrets(namespace, saSecretName).getUserAccessToken());
        newNsUser.setUserType(AUTH_NAMESPACE_ADMIN);
        newNsUser.setIsActive(CHECK_Y);

        ResultStatus createCpUserResult = usersService.createUsers(newNsUser);

        if (createCpUserResult.getResultCode().equalsIgnoreCase(RESULT_STATUS_FAIL)) {
            resourceYamlService.deleteNamespaceYaml(params);
            return createCpUserResult;
        }


        for (String rq : initTemplate.getResourceQuotasList()) {
            if (propertyService.getResourceQuotasList().contains(rq)) {
                params.setRs_rq(rq);
                resourceYamlService.createDefaultResourceQuota(params);
            }
        }

        for (String lr : initTemplate.getLimitRangesList()) {
            if (propertyService.getLimitRangesList().contains(lr)) {
                params.setRs_lr(lr);
                resourceYamlService.createDefaultLimitRanges(params);
            }
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(createCpUserResult, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_LIST_PAGE");
    }


    /**
     * Namespaces 수정(Update Namespaces)
     *
     * @param params       the params
     * @param initTemplate the init template
     * @return the resultStatus
     */
    public ResultStatus modifyInitNamespaces(Params params, NamespacesInitTemplate initTemplate) {

        String cluster = params.getCluster();
        String namespace = params.getNamespace();


        // 1. namespace 일치 여부 확인
        if (!namespace.equals(initTemplate.getName())) {
            return resultStatusService.NOT_MATCH_NAMESPACES();
        }

        // 2. namespace 관리자 지정 여부 확인
        String updateNsAdminUserId = initTemplate.getNsAdminUserId();
        if (updateNsAdminUserId.trim().isEmpty() || updateNsAdminUserId == null) {
            return resultStatusService.REQUIRES_NAMESPACE_ADMINISTRATOR_ASSIGNMENT();
        }


        // 3. namespace 관리자 cp-temp-namespace 컬럼 존재 여부 확인
        Users newNsUser = null;
        try {
            newNsUser = usersService.getUsers(cluster, propertyService.getDefaultNamespace(), updateNsAdminUserId);
        } catch (NullPointerException e) {
            LOGGER.info("THERE ARE NO USERS IN THE TEMP NAMESPACE.....");
            return resultStatusService.UNAPPROACHABLE_USERS();

        }


        // 4. namespace 관리자 여부 확인
        Users nsAdminUser = null;
        try {
            nsAdminUser = usersService.getUsersByNamespaceAndNsAdmin(cluster, namespace);
        } catch (NullPointerException e) {
            LOGGER.info("NAMESPACE ADMINISTRATOR DOES NOT EXIST...");
        }


        String updateNsAdminUserSA = newNsUser.getServiceAccountName();

        // 5. 현재 namespace 관리자가 존재하지만, 다른 사용자를 관리자로 변경할 경우
        // 현재 namespace 관리자는 'USER' 권한으로 USER-TYPE 변경
        if (nsAdminUser != null && !nsAdminUser.getUserId().equals(initTemplate.getNsAdminUserId())) {
            LOGGER.info("THE CURRENT NAMESPACE ADMINISTRATOR EXISTS AND CHANGES TO A NEW NAMESPACE ADMINISTRATOR....");
            //Changes the current namespace admin user-type to 'USER'
            nsAdminUser.setUserType(AUTH_USER);
            usersService.updateUsers(nsAdminUser);
        }

        // 6. 현재 namespace 관리자 존재하지 않으며, 다른 사용자를 관리자로 변경할 경우
        if (nsAdminUser == null || !nsAdminUser.getUserId().equals(initTemplate.getNsAdminUserId())) {

            LOGGER.info("WHEN THE CURRENT NAMESPACE ADMINISTRATOR DOES NOT EXIST OR CHANGES TO A NEW NAMESPACE ADMINISTRATOR.....");

            // Delete the existing user account
            Users newNamespaceAdmin = null;
            try {
                // Verify that the new namespace admin is the current namespace member
                newNamespaceAdmin = usersService.getUsers(cluster, namespace, updateNsAdminUserId);
            } catch (NullPointerException e) {
                LOGGER.info("THE NEW NAMESPACE ADMINISTRATOR IS NOT A CURRENT NAMESPACE MEMBER.....");
            }

            if (newNamespaceAdmin != null) {
                // If the new namespace admin is current a namespace member, it deletes the user.
         //       usersService.deleteUsers(newNamespaceAdmin);

            }

            // create admin and init role
            resourceYamlService.createInitRole(params);
            resourceYamlService.createAdminRole(params);

            params.setRs_sa(updateNsAdminUserSA);
            params.setRs_role(propertyService.getAdminRole());

            // 7. Service Account 생성
            ResultStatus createSAresult = resourceYamlService.createServiceAccount(params);
            if (createSAresult.getResultCode().equalsIgnoreCase(RESULT_STATUS_FAIL)) {
                return createSAresult;
            }

            // 8. Role-Binding 생성
            ResultStatus createRBresult = resourceYamlService.createRoleBinding(params);

            if (createRBresult.getResultCode().equalsIgnoreCase(RESULT_STATUS_FAIL)) {
                LOGGER.info("ROLE BINDING EXECUTE IS FAILED. K8S SA AND RB WILL BE REMOVED...");
                //   resourceYamlService.deleteServiceAccountAndRolebinding(namespace, updateNsAdminUserSA, propertyService.getAdminRole());
                return createRBresult;
            }

            String saSecretName = resourceYamlService.getSecretName(params);

            newNsUser.setId(0);
            newNsUser.setCpNamespace(namespace);
            newNsUser.setRoleSetCode(propertyService.getAdminRole());
            newNsUser.setSaSecret(saSecretName);
            newNsUser.setSaToken(accessTokenService.getSecrets(namespace, saSecretName).getUserAccessToken());
            newNsUser.setUserType(AUTH_NAMESPACE_ADMIN);
            newNsUser.setIsActive(CHECK_Y);
            usersService.createUsers(newNsUser);

        }

        // Modify ResourceQuotas , LimitRanges
        // modifyResourceQuotas(namespace, initTemplate.getResourceQuotasList());
        // modifyLimitRanges(namespace, initTemplate.getLimitRangesList());


        return (ResultStatus) commonService.setResultModelWithNextUrl(resultStatusService.SUCCESS_RESULT_STATUS(), Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_DETAIL_PAGE");
    }


    /**
     * ResourceQuotas 변경(Modify ResourceQuotas)
     *
     * @param namespace            the namespace
     * @param requestUpdatedRqList the request update resourceQuotas list
     */
    private void modifyResourceQuotas(String namespace, List<String> requestUpdatedRqList) {
        ResourceQuotasList resourceQuotasList = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasListUrl()
                        .replace("{namespace}", namespace), HttpMethod.GET, null, ResourceQuotasList.class);

        List<String> k8sResourceQuotasList = resourceQuotasList.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sResourceQuotasList, requestUpdatedRqList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedRqList, k8sResourceQuotasList);

        // delete
        for (String deleteRqName : toBeDelete) {
            Params params = new Params();
            params.setNamespace(namespace);
            params.setResourceName(deleteRqName);
            resourceQuotasService.deleteResourceQuotas(params);
        }
        // add
        for (String rqName : toBeAdd) {
            // resourceYamlService.createDefaultResourceQuota(namespace, rqName);
        }
    }


    /**
     * LimitRanges 변경(Modify LimitRanges)
     *
     * @param namespace            the namespace
     * @param requestUpdatedLrList the request update limitRanges list
     */
    private void modifyLimitRanges(String namespace, List<String> requestUpdatedLrList) {
        LimitRangesList limitRangesList = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListUrl().replace("{namespace}", namespace),
                HttpMethod.GET, null, LimitRangesList.class);

        List<String> k8sLimitRangesList = limitRangesList.getItem().stream().map(lr -> lr.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sLimitRangesList, requestUpdatedLrList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedLrList, k8sLimitRangesList);

        for (String lrName : toBeAdd) {
            //  resourceYamlService.createDefaultLimitRanges(namespace, lrName);
        }

        for (String deleteLrName : toBeDelete) {
            //  limitRangesService.deleteLimitRanges(namespace, deleteLrName);
        }
    }


    /**
     * Namespaces SelectBox 를 위한 Namespaces 목록 조회(Get Namespaces list for SelectBox)
     *
     * @param params the params
     * @return the namespaces list
     */
    public Object getNamespacesListForSelectbox(Params params) {
        params.setOrderBy("name");
        params.setOrder("asc");

        NamespacesList namespacesList = getNamespacesList(params);
        List<NamespacesListItem> namespaceItem = namespacesList.getItems();

        List<String> returnNamespaceList = new ArrayList<>();

        //add 'all'
        returnNamespaceList.add(ALL_NAMESPACES);

        for (NamespacesListItem n : namespaceItem) {
            returnNamespaceList.add(n.getName());
        }

        NamespacesListSupport namespacesListSupport = new NamespacesListSupport();
        namespacesListSupport.setItems(returnNamespaceList);

        return commonService.setResultModel(namespacesListSupport, Constants.RESULT_STATUS_SUCCESS);
    }



    public UsersList getMappingNamespacesListByAdmin(Params params) {
        NamespacesList namespacesList = getNamespacesList(params);
        List<Users> items = namespacesList.getItems().stream().map(x -> new Users(x.getName())).collect(Collectors.toList());
        items.add(0, new Users(Constants.ALL_NAMESPACES.toUpperCase()));
        UsersList usersList = new UsersList(items);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }

}