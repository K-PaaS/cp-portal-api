package org.container.platform.api.roles;

import io.swagger.annotations.*;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Roles Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
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
     * @param params the params
     * @return the roles list
     */
    @ApiOperation(value = "Roles 목록 조회(Get Roles list)", nickname = "getRolesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping
    public RolesList getRolesList(Params params) {
        return rolesService.getRolesList(params);
    }


    /**
     * Roles 상세 조회(Get Roles detail)
     *
     * @param params the params
     * @return the roles detail
     */
    @ApiOperation(value = "Roles 상세 조회(Get Roles detail)", nickname = "getRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Roles getRoles(Params params) {
        return rolesService.getRoles(params);
    }


    /**
     * Roles YAML 조회(Get Roles yaml)
     *
     * @param params the params
     * @return the roles yaml
     */
    @ApiOperation(value = "Roles YAML 조회(Get Roles yaml)", nickname = "getRolesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getRolesYaml(Params params) {
        return rolesService.getRolesYaml(params);
    }


    /**
     * Roles 생성(Create Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 생성(Create Roles)", nickname = "createRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @PostMapping
    public Object createRoles(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return rolesService.createRoles(params);
    }


    /**
     * Roles 삭제(Delete Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 삭제(Delete Roles)", nickname = "deleteRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteRoles(Params params) {

        return rolesService.deleteRoles(params);
    }


    /**
     * Roles 수정(Update Roles)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "Roles 수정(Update Roles)", nickname = "updateRoles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateRoles(@RequestBody Params params) {
        return rolesService.updateRoles(params);
    }

}
