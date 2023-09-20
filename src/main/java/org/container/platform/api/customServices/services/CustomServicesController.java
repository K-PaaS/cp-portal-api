package org.container.platform.api.customServices.services;

import io.swagger.annotations.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CustomServices Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Api(value = "CustomServicesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/services")
public class CustomServicesController {

    private final CustomServicesService customServicesService;

    /**
     * Instantiates a new CustomServices controller
     *
     * @param customServicesService the customServices service
     */
    @Autowired
    public CustomServicesController(CustomServicesService customServicesService) {
        this.customServicesService = customServicesService;
    }


    /**
     * Services 목록 조회(Get Services list)
     *
     * @param params the params
     * @return the services list
     */
    @ApiOperation(value = "Services 목록 조회(Get Services list)", nickname = "getCustomServicesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public CustomServicesList getCustomServicesList(Params params) {
            return customServicesService.getCustomServicesList(params);
    }


    /**
     * Services 상세 조회(Get Services detail)
     *
     * @param params the params
     * @return the services detail
     */
    @ApiOperation(value = "Services 상세 조회(Get Services detail)", nickname = "getCustomServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getCustomServices(Params params) {
            return customServicesService.getCustomServices(params);
    }


    /**
     * Services YAML 조회(Get Services yaml)
     *
     * @param params the params
     * @return the services yaml
     */
    @ApiOperation(value = "Services YAML 조회(Get Services yaml)", nickname = "getCustomServicesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getCustomServicesYaml(Params params) {
            return customServicesService.getCustomServicesYaml(params);
    }


    /**
     * Services 생성(Create Services)
     *
     * @param params the params
     * @return the resultStatus
     */

    @ApiOperation(value = "Services 생성(Create Services)", nickname = "createServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public Object createServices(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return customServicesService.createServices(params);
    }


    /**
     * Services 삭제(Delete Services)
     *
     * @param params the params
     * @return the resultStatus
     */

    @ApiOperation(value = "Services 삭제(Delete Services)", nickname = "deleteServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteServices(Params params) {
        return customServicesService.deleteServices(params);
    }


    /**
     * Services 수정(Update Services)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Services 수정(Update Services)", nickname = "updateServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateServices(@RequestBody Params params) {
        return customServicesService.updateServices(params);
    }

}
