package org.paasta.container.platform.api.roles;

import io.swagger.annotations.*;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;


/**
 * Roles Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.13
 */
@Api(value = "RoleController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/roles")
public class RolesController {

    private final RolesService rolesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Roles controller
     *
     * @param rolesService the roles service
     */
    @Autowired
    public RolesController(RolesService rolesService, ResultStatusService resultStatusService) {
        this.rolesService = rolesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Roles 목록 조회(Get Roles list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the roles list
     */
    @ApiOperation(value = "Roles 목록 조회(Get Roles list)", nickname = "getRolesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping
    public Object getRolesList(@PathVariable(value = "cluster") String cluster,
                               @PathVariable(value = "namespace") String namespace,
                               @RequestParam(required = false, defaultValue = "0") int offset,
                               @RequestParam(required = false, defaultValue = "0") int limit,
                               @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                               @RequestParam(required = false, defaultValue = "") String order,
                               @RequestParam(required = false, defaultValue = "") String searchName,
                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return rolesService.getRolesListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return rolesService.getRolesListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return rolesService.getRolesList(namespace, offset, limit, orderBy, order, searchName);
    }


    /**
     * Roles 상세 조회(Get Roles detail)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the roles detail
     */
    @ApiOperation(value = "Roles 상세 조회(Get Roles detail)", nickname = "getRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getRoles(@PathVariable(value = "cluster") String cluster,
                           @PathVariable(value = "namespace") String namespace,
                           @PathVariable(value = "resourceName") String resourceName,
                           @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return rolesService.getRolesAdmin(namespace, resourceName);
        }

        return rolesService.getRoles(namespace, resourceName);
    }


    /**
     * Roles YAML 조회(Get Roles yaml)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the roles yaml
     */
    @ApiOperation(value = "Roles YAML 조회(Get Roles yaml)", nickname = "getRolesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getRolesYaml(@PathVariable(value = "cluster") String cluster,
                               @PathVariable(value = "namespace") String namespace,
                               @PathVariable(value = "resourceName") String resourceName,
                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return rolesService.getRolesAdminYaml(namespace, resourceName, new HashMap<>());
        }

        return rolesService.getRolesYaml(namespace, resourceName, new HashMap<>());
    }


    /**
     * Roles 생성(Create Roles)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @param yaml      the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 생성(Create Roles)", nickname = "createRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createRoles(@PathVariable(value = "cluster") String cluster,
                              @PathVariable(value = "namespace") String namespace,
                              @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                              @RequestBody String yaml) throws Exception {

        if (isAdmin) {

            if (yaml.contains("---")) {
                Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, true);
                return object;
            }

            return rolesService.createRoles(namespace, yaml, true);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * Roles 삭제(Delete Roles)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 삭제(Delete Roles)", nickname = "deleteRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteRoles(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @PathVariable(value = "resourceName") String resourceName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return rolesService.deleteRoles(namespace, resourceName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * Roles 수정(Update Roles)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @param yaml         the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 수정(Update Roles)", nickname = "updateRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateRoles(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @PathVariable(value = "resourceName") String resourceName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                    @RequestBody String yaml) {

        if (isAdmin) {
            return rolesService.updateRoles(namespace, resourceName, yaml);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

}
