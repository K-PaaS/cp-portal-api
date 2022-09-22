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
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.ResultStatusException;
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
                propertyService.getCpMasterApiListNamespacesDeleteUrl().replace("{name:.+}", params.getNamespace()),
                HttpMethod.DELETE, null, ResultStatus.class, params);

        usersService.deleteNamespaceAllUsers(params);

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
        if (initTemplate.getName().equalsIgnoreCase(NULL_REPLACE_TEXT)) {
            throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
        }

        for(String namespace : propertyService.getExceptNamespaceList()) {
            if(namespace.equalsIgnoreCase(initTemplate.getName())) {
                throw new ResultStatusException(MessageConstant.NOT_ALLOWED_RESOURCE_NAME.getMsg()); }
        }

        params.setNamespace(initTemplate.getName());

        // 1. namespace 생성
        resourceYamlService.createNamespace(params);

        // 2. init-role, admin-role 생성
        resourceYamlService.createInitRole(params);
        resourceYamlService.createAdminRole(params);

        List<String> resourceQuotasList = initTemplate.getResourceQuotasList().stream().distinct().collect(Collectors.toList());
        List<String> limitRangesList = initTemplate.getLimitRangesList().stream().distinct().collect(Collectors.toList());

        // 3. resourceQuotas 생성
        for (String rq : resourceQuotasList) {
            if (propertyService.getResourceQuotasList().contains(rq)) {
                params.setRs_rq(rq);
                resourceYamlService.createDefaultResourceQuota(params);
            }
        }

        // 4. limitRanges 생성
        for (String lr : limitRangesList) {
            if (propertyService.getLimitRangesList().contains(lr)) {
                params.setRs_lr(lr);
                resourceYamlService.createDefaultLimitRanges(params);
            }
        }

        return (ResultStatus) commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespaces 수정(Update Namespaces)
     *
     * @param params       the params
     * @param initTemplate the init template
     * @return the resultStatus
     */
    public ResultStatus modifyInitNamespaces(Params params, NamespacesInitTemplate initTemplate) {

        if (initTemplate.getName().equalsIgnoreCase(NULL_REPLACE_TEXT)) {
            throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
        }

        if (!initTemplate.getName().equals(params.getNamespace())) {
            throw new ResultStatusException(MessageConstant.NOT_MATCH_NAMESPACES.getMsg());
        }

        try {
            resourceYamlService.createAdminRole(params);
        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED WHILE CREATE DEFAULT ADMIN ROLE...");
        }


        try {
            resourceYamlService.createInitRole(params);
        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURRED WHILE CREATE DEFAULT INIT ROLE...");
        }


        // Modify ResourceQuotas , LimitRanges
        try {
            List<String> resourceQuotasList = initTemplate.getResourceQuotasList().stream().distinct().collect(Collectors.toList());
            List<String> limitRangesList = initTemplate.getLimitRangesList().stream().distinct().collect(Collectors.toList());
            modifyResourceQuotas(params, resourceQuotasList);
            modifyLimitRanges(params, limitRangesList);

        } catch (Exception e) {
            throw new ResultStatusException(CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
        }


        return (ResultStatus) commonService.setResultModel(new ResultStatus(), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ResourceQuotas 변경(Modify ResourceQuotas)
     *
     * @param params               the params
     * @param requestUpdatedRqList the request update resourceQuotas list
     */
    private void modifyResourceQuotas(Params params, List<String> requestUpdatedRqList) {
        ResourceQuotasList resourceQuotasList = resourceQuotasService.getResourceQuotasList(params);
        List<String> k8sResourceQuotasNameList = resourceQuotasList.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());
        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sResourceQuotasNameList, requestUpdatedRqList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedRqList, k8sResourceQuotasNameList);

        // delete
        for (String deleteRq : toBeDelete) {
            params.setResourceName(deleteRq);
            resourceQuotasService.deleteResourceQuotas(params);
        }

        // add
        for (String createRq : toBeAdd) {
            params.setRs_rq(createRq);
            resourceYamlService.createDefaultResourceQuota(params);
        }
    }


    /**
     * LimitRanges 변경(Modify LimitRanges)
     *
     * @param params               the params
     * @param requestUpdatedLrList the request update limitRanges list
     */
    private void modifyLimitRanges(Params params, List<String> requestUpdatedLrList) {
        LimitRangesList limitRangesList = limitRangesService.getLimitRangesList(params);
        List<String> k8sLimitRangesNameList = limitRangesList.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = commonService.compareArrayList(k8sLimitRangesNameList, requestUpdatedLrList);
        ArrayList<String> toBeAdd = commonService.compareArrayList(requestUpdatedLrList, k8sLimitRangesNameList);

        // delete
        for (String deleteLr : toBeDelete) {
            params.setResourceName(deleteLr);
            limitRangesService.deleteLimitRanges(params);
        }

        // add
        for (String createLr : toBeAdd) {
            params.setRs_lr(createLr);
            resourceYamlService.createDefaultLimitRanges(params);
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