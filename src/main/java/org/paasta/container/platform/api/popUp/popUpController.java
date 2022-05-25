package org.paasta.container.platform.api.popUp;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.paasta.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.roles.RolesService;
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
        return resourceQuotasService.getRqDefaultList(params);
    }


    /**
     * LimitRanges Template 목록 조회(Get LimitRanges Template list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the limitRangesDefault list
     */
    @ApiOperation(value = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", nickname = "getLimitRangesTemplateList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/limitRanges/template")
    public Object getLimitRangesTemplateList(@PathVariable(value = "cluster") String cluster,
                                             @PathVariable(value = "namespace") String namespace,
                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                             @RequestParam(required = false, defaultValue = "0") int limit,
                                             @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                             @RequestParam(required = false, defaultValue = "") String order,
                                             @RequestParam(required = false, defaultValue = "") String searchName,
                                             @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return limitRangesService.getLimitRangesTemplateList(namespace, offset, limit, orderBy, order, searchName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
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
    @GetMapping("/users/{userId:.+}/namespacesRolesList")
    public Object getNamespacesRolesTemplateList(Params params) {
        if (params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
            return rolesService.getNamespacesRolesTemplateList(params);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();

    }
}