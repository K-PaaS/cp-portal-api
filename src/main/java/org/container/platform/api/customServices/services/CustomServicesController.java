package org.container.platform.api.customServices.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "CustomServicesController v1")
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
    @Operation(summary = "Services 목록 조회(Get Services list)", operationId = "getCustomServicesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Services 상세 조회(Get Services detail)", operationId = "getCustomServices")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Services YAML 조회(Get Services yaml)", operationId = "getCustomServicesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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

    @Operation(summary = "Services 생성(Create Services)", operationId = "createServices")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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

    @Operation(summary = "Services 삭제(Delete Services)", operationId = "deleteServices")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Services 수정(Update Services)", operationId = "updateServices")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateServices(@RequestBody Params params) {
        return customServicesService.updateServices(params);
    }

}
