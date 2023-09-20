package org.container.platform.api.customServices.ingresses;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.container.platform.api.customServices.services.CustomServices;
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
@Api(value = "IngressesController v1")
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
    @ApiOperation(value = "Ingresses 목록 조회(Get Ingresses list)", nickname = "getIngressesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "Ingresses 상세 조회(Get Ingresses detail)", nickname = "getIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "Ingresses YAML 조회(Get Ingresses yaml)", nickname = "getIngressesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "Ingresses 생성(Create Ingresses)", nickname = "createIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "Ingresses 삭제(Delete Ingresses)", nickname = "deleteIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "Ingresses 수정(Update Ingresses)", nickname = "updateIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateIngresses(@RequestBody Params params) {
        return ingressesService.updateIngresses(params);
    }


}
