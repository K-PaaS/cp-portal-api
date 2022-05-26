package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ResourceQuotas Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 **/
@Api(value = "ResourceQuotasController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/resourceQuotas")
public class ResourceQuotasController {
    private final ResourceQuotasService resourceQuotasService;
    private final ResultStatusService resultStatusService;


    /**
     * Instantiates a ResourceQuotas Controller
     *
     * @param resourceQuotasService the resourceQuotas Service
     */
    @Autowired
    public ResourceQuotasController(ResourceQuotasService resourceQuotasService, ResultStatusService resultStatusService) {
        this.resourceQuotasService = resourceQuotasService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * ResourceQuotas 목록 조회(Get ResourceQuotas list)
     * @param params the params
     * @return the resourceQuotas list
     */
    @ApiOperation(value = "ResourceQuotas 목록 조회(Get ResourceQuotas list)", nickname = "getResourceQuotasList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public ResourceQuotasList getResourceQuotasList(Params params) {
        return resourceQuotasService.getResourceQuotasList(params);
    }


    /**
     * ResourceQuotas 상세 조회(Get ResourceQuotas detail)
     * @param params the params
     * @return the resourceQuotas detail
     */
    @ApiOperation(value = "ResourceQuotas 상세 조회(Get ResourceQuotas detail)", nickname = "getResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public ResourceQuotas getResourceQuotas(Params params) {
        return resourceQuotasService.getResourceQuotas(params);
    }


    /**
     * ResourceQuotas YAML 조회(Get ResourceQuotas yaml)
     * @param params the params
     * @return the resourceQuotas yaml
     */
    @ApiOperation(value = "ResourceQuotas YAML 조회(Get ResourceQuotas yaml)", nickname = "getResourceQuotasYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "{resourceName:.+}/yaml")
    public CommonResourcesYaml getResourceQuotasYaml(Params params) {
        return resourceQuotasService.getResourceQuotasYaml(params);
    }


    /**
     * ResourceQuotas 생성(Create ResourceQuotas)
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 생성(Create ResourceQuotas)", nickname = "createResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public Object createResourceQuotas(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return resourceQuotasService.createResourceQuotas(params);

    }


    /**
     * ResourceQuotas 삭제(Delete ResourceQuotas)
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 삭제(Delete ResourceQuotas)", nickname = "deleteResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{resourceName:.+}")
    public ResultStatus deleteResourceQuotas(Params params) {
        return resourceQuotasService.deleteResourceQuotas(params);
    }


    /**
     * ResourceQuotas 수정(Update ResourceQuotas)
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 수정(Update ResourceQuotas)", nickname = "updateResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/{resourceName:.+}")
    public ResultStatus updateResourceQuotas(@RequestBody Params params) {
        return resourceQuotasService.updateResourceQuotas(params);
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
    @GetMapping(value = "/template")
    public Object getResourceQuotasDefaultList(Params params) throws JsonProcessingException {
        return resourceQuotasService.getRqDefaultList(params);
    }


}
