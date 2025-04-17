package org.container.platform.api.customServices.ingresses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.container.platform.api.customServices.services.CustomServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Ingresses Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Tag(name = "IngressesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/ingresses")
public class IngressesController {

    private final IngressesService ingressesService;
    private final CustomServicesService customServicesService;

    /**
     * Instantiates a new Ingresses controller
     *
     * @param ingressesService the ingresses service
     * @param customServicesService
     */
    @Autowired
    public IngressesController(IngressesService ingressesService, CustomServicesService customServicesService){
        this.ingressesService = ingressesService;
        this.customServicesService = customServicesService;
    }

    /**
     * Ingresses 목록 조회(Get Ingresses list)
     *
     * @param params the params
     * @return the ingresses list
     */
    @Operation(summary = "Ingresses 목록 조회(Get Ingresses list)", operationId = "getIngressesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public IngressesList getIngressesList(Params params){
        return ingressesService.getIngressesList(params);
    }

    /**
     * Ingresses 상세 조회(Get Ingresses detail)
     *
     * @param params the params
     * @return the ingresses detail
     */
    @Operation(summary = "Ingresses 상세 조회(Get Ingresses detail)", operationId = "getIngresses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Ingresses getIngresses(Params params){
            return ingressesService.getIngresses(params);
    }

    /**
     * Ingresses YAML 조회(Get Ingresses yaml)
     *
     * @param params the params
     * @return the Ingresses yaml
     */
    @Operation(summary = "Ingresses YAML 조회(Get Ingresses yaml)", operationId = "getIngressesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getIngressesYaml(Params params){
            return ingressesService.getIngressesYaml(params);
    }

    /**
     * Ingresses 생성(Create Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Ingresses 생성(Create Ingresses)", operationId = "createIngresses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createIngresses(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return ingressesService.createIngresses(params);
    }

    /**
     * Ingresses 삭제(Delete Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Ingresses 삭제(Delete Ingresses)", operationId = "deleteIngresses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteIngresses(Params params) {
        return ingressesService.deleteIngresses(params);
    }


    /**
     * Ingresses 수정(Update Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Ingresses 수정(Update Ingresses)", operationId = "updateIngresses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateIngresses(@RequestBody Params params) {
        return ingressesService.updateIngresses(params);
    }


}
