package org.paasta.container.platform.api.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesDefault;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesDefaultList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefault;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasDefaultList;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import static org.paasta.container.platform.api.common.Constants.*;

/**
 * Resource Yaml Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.14
 **/
@Service
public class ResourceYamlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceYamlService.class);

    private final CommonService commonService;
    private final PropertyService propertyService;
    private final TemplateService templateService;
    private final RestTemplateService restTemplateService;
    private final ResourceQuotasService resourceQuotasService;

    @Autowired
    public ResourceYamlService(CommonService commonService, PropertyService propertyService, TemplateService templateService, RestTemplateService restTemplateService, ResourceQuotasService resourceQuotasService) {
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.templateService = templateService;
        this.restTemplateService = restTemplateService;
        this.resourceQuotasService = resourceQuotasService;
    }


    /**
     * ftl 파일로 Namespace 생성(Create Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createNamespace(Params params) {
        Map map = new HashMap();
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_namespace.ftl", map));
        ResultStatus  resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 Service Account 생성(Create Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createServiceAccount(Params params) {
        Map map = new HashMap();
        map.put("userName", params.getRs_sa());
        map.put("spaceName", params.getNamespace());
        params.setYaml(templateService.convert("create_account.ftl", map));
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * ftl 파일로 Role Binding 생성(Create Role Binding)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createRoleBinding(Params params) {
        Map map = new HashMap();


        if(params.getRs_role().equalsIgnoreCase("")) {
            map.put("userName", params.getRs_sa());
            map.put("spaceName", params.getNamespace());
            params.setYaml(templateService.convert("create_clusterRoleBinding.ftl", map));
        } else {
            map.put("userName", params.getRs_sa());
            map.put("roleName", params.getRs_role());
            map.put("spaceName", params.getNamespace());
            params.setYaml(templateService.convert("create_roleBinding.ftl", map));
        }

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRoleBindingsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 init role 생성(Create init role)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createInitRole(Params params) {
        // init role 생성
        Map<String, Object> map = new HashMap();
        map.put("spaceName", params.getNamespace());
        map.put("roleName", propertyService.getInitRole());

        params.setYaml(templateService.convert("create_init_role.ftl", map));
        ResultStatus  resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ftl 파일로 admin role 생성(Create admin role)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createAdminRole(Params params) {
        Map<String, Object> map = new HashMap();
        map.put("spaceName", params.getNamespace());
        map.put("roleName", propertyService.getAdminRole());

        params.setYaml(templateService.convert("create_admin_role.ftl", map));

        ResultStatus  resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListRolesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Namespace 에 ResourceQuotas 를 할당(Allocate ResourceQuotas to Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createDefaultResourceQuota(Params params) {
        ResourceQuotasDefaultList resourceQuotasDefaultList = restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class);
        String limitsCpu = "";
        String limitsMemory = "";

        String rqName = params.getRs_rq();

        for (ResourceQuotasDefault d:resourceQuotasDefaultList.getItems()) {
            if (rqName.equals("")) {
                rqName = propertyService.getLowResourceQuotas();
            }

            if (propertyService.getResourceQuotasList().contains(rqName) && d.getName().equals(rqName)) {
                limitsCpu = d.getLimitCpu();
                limitsMemory = d.getLimitMemory();

                break;
            }
        }
        Map<String, Object> model = new HashMap<>();
        model.put("name", rqName);
        model.put("namespace", params.getNamespace());
        model.put("limits_cpu", limitsCpu);
        model.put("limits_memory", limitsMemory);

        params.setYaml(templateService.convert("create_resource_quota.ftl", model));

        ResultStatus  resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespace 에 LimitRanges 를 할당(Allocate LimitRanges to Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createDefaultLimitRanges(Params params) {
        LimitRangesDefaultList limitRangesDefaultList = restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class);
        String limitsCpu = "";
        String limitsMemory = "";

        String lrName = params.getRs_lr();

        for (LimitRangesDefault limitRanges:limitRangesDefaultList.getItems()) {
            if (lrName.equals("")) {
                lrName = propertyService.getLowLimitRanges();
            }

            if (propertyService.getLimitRangesList().contains(lrName) && limitRanges.getName().equals(lrName)) {
                if(Constants.SUPPORTED_RESOURCE_CPU.equals(limitRanges.getResource())) {
                    limitsCpu = limitRanges.getDefaultLimit();
                } else {
                    limitsMemory = limitRanges.getDefaultLimit();
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("name", lrName);
        model.put("namespace", params.getNamespace());
        model.put("limit_cpu", limitsCpu);
        model.put("limit_memory", limitsMemory);

        params.setYaml(templateService.convert("create_limit_range.ftl", model));

        ResultStatus  resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }




    /**
     * ftl 파일로 ClusterRole Binding 생성(Create ClusterRole Binding)
     *
     * @param username
     * @param namespace
     * @return
     */
    public ResultStatus createClusterRoleBinding(String username, String namespace) {
        Map map = new HashMap();
        String roleBindingYaml;

        map.put("userName", username);
        map.put("spaceName", namespace);

        roleBindingYaml = templateService.convert("create_clusterRoleBinding.ftl", map);

        Object rbResult = restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListClusterRoleBindingsCreateUrl(), HttpMethod.POST, roleBindingYaml, Object.class, true);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(rbResult, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, null);
    }

    /**
     * ServiceAccount 와 RoleBinding 삭제 (Delete ServiceAccount and Role binding)
     *
     * @param username
     * @param namespace
     * @return
     */
    public void deleteServiceAccountAndRolebinding(String namespace, String username, String roleName) {
        // 1. SA 삭제
        restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersDeleteUrl()
                .replace("{namespace}", namespace).replace("{name}", username), HttpMethod.DELETE, null, Object.class, true);

        // 2. RB 삭제
        restTemplateService.sendYaml(TARGET_CP_MASTER_API, propertyService.getCpMasterApiListRoleBindingsDeleteUrl()
                .replace("{namespace}", namespace)
                .replace("{name}", username + Constants.NULL_REPLACE_TEXT + roleName + "-binding"), HttpMethod.DELETE, null, Object.class, true);
    }


    /**
     * Namespace 삭제 (Delete Namespace)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteNamespaceYaml(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesDeleteUrl().replace("{name}", params.getNamespace()), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * service account 의 secret 이름을 조회(Get Secret of Service Account)
     *
     * @param params the params
     * @return the resultStatus
     */
    public String getSecretName(Params params) {
        String jsonObj = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListUsersGetUrl().replace("{namespace}", params.getNamespace())
                        .replace("{name}", params.getRs_sa()), HttpMethod.GET, null, String.class, params);

        JsonObject jsonObject = JsonParser.parseString(jsonObj).getAsJsonObject();
        JsonElement element = jsonObject.getAsJsonObject().get("secrets");
        element = element.getAsJsonArray().get(0);
        String token = element.getAsJsonObject().get("name").toString();
        token = token.replaceAll("\"", "");
        return token;
    }


}