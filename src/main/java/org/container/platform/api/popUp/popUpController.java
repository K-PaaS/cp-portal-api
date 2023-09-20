package org.container.platform.api.popUp;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.roles.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * PopUp Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 **/
@RestController
@RequestMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/popup")
public class popUpController {

    private final ResourceQuotasService resourceQuotasService;
    private final LimitRangesService limitRangesService;
    private final RolesService rolesService;
    private final ResultStatusService resultStatusService;

    @Autowired
    public popUpController(ResourceQuotasService resourceQuotasService, LimitRangesService limitRangesService, RolesService rolesService, ResultStatusService resultStatusService) {
        this.resourceQuotasService = resourceQuotasService;
        this.limitRangesService = limitRangesService;
        this.rolesService = rolesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)
     * @param params the params
     * @return the resourceQuota list
     * @throws JsonProcessingException
     */
    @ApiOperation(value = "ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)", nickname = "getResourceQuotasDefaultList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/resourceQuotas/template")
    public Object getResourceQuotasDefaultList(Params params) throws JsonProcessingException {
        if(params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
            return resourceQuotasService.getResourceQuotasDefaultTemplateList(params);
        }
        return resourceQuotasService.getResourceQuotasTemplateList(params);
    }


    /**
     * LimitRanges Template 목록 조회(Get LimitRanges Template list)
     *
     * @param params the params
     * @return the limitRangesDefault list
     */
    @ApiOperation(value = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", nickname = "getLimitRangesTemplateList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/limitRanges/template")
    public Object getLimitRangesTemplateList(Params params) {
        if(params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
            return limitRangesService.getLimitRangesDefaultTemplateList(params);
        }
        return limitRangesService.getLimitRangesTemplateList(params);
    }

    /**
     * User 가 속해 있는 Namespace 와 Role 목록 조회(Get Namespace and Roles List to which User belongs)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "User 가 속해 있는 Namespace 와 Role 목록 조회(Get Namespace and Roles List to which User belongs)", nickname = "getRolesListAllNamespacesAdminByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/users/{userAuthId:.+}/namespacesRolesList")
    public Object getNamespacesRolesTemplateList(Params params) {
        return rolesService.getNamespacesRolesTemplateList(params);
    }
}