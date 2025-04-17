package org.container.platform.api.popUp;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.roles.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * PopUp Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 **/

@Tag(name = "PopUpController v1")
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
    @Operation(summary = "ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)", operationId = "getResourceQuotasDefaultList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", operationId = "getLimitRangesTemplateList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "User 가 속해 있는 Namespace 와 Role 목록 조회(Get Namespace and Roles List to which User belongs)", operationId = "getRolesListAllNamespacesAdminByUserId")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping("/users/{userAuthId:.+}/namespacesRolesList")
    public Object getNamespacesRolesTemplateList(Params params) {
        return rolesService.getNamespacesRolesTemplateList(params);
    }
}