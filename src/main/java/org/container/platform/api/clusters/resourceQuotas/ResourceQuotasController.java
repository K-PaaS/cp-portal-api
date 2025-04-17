package org.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ResourceQuotas Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 **/
@Tag(name = "ResourceQuotasController v1")
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
    @Operation(summary = "ResourceQuotas 목록 조회(Get ResourceQuotas list)", operationId = "getResourceQuotasList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public ResourceQuotasList getResourceQuotasList(Params params) {
        return resourceQuotasService.getResourceQuotasList(params);
    }


    /**
     * ResourceQuotas 상세 조회(Get ResourceQuotas detail)
     * @param params the params
     * @return the resourceQuotas detail
     */
    @Operation(summary = "ResourceQuotas 상세 조회(Get ResourceQuotas detail)", operationId = "getResourceQuotas")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public ResourceQuotas getResourceQuotas(Params params) {
        return resourceQuotasService.getResourceQuotas(params);
    }


    /**
     * ResourceQuotas YAML 조회(Get ResourceQuotas yaml)
     * @param params the params
     * @return the resourceQuotas yaml
     */
    @Operation(summary = "ResourceQuotas YAML 조회(Get ResourceQuotas yaml)", operationId = "getResourceQuotasYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "{resourceName:.+}/yaml")
    public CommonResourcesYaml getResourceQuotasYaml(Params params) {
        return resourceQuotasService.getResourceQuotasYaml(params);
    }


    /**
     * ResourceQuotas 생성(Create ResourceQuotas)
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "ResourceQuotas 생성(Create ResourceQuotas)", operationId = "createResourceQuotas")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ResourceQuotas 삭제(Delete ResourceQuotas)", operationId = "deleteResourceQuotas")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping(value = "/{resourceName:.+}")
    public ResultStatus deleteResourceQuotas(Params params) {
        return resourceQuotasService.deleteResourceQuotas(params);
    }


    /**
     * ResourceQuotas 수정(Update ResourceQuotas)
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "ResourceQuotas 수정(Update ResourceQuotas)", operationId = "updateResourceQuotas")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)", operationId = "getResourceQuotasDefaultList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/template")
    public Object getResourceQuotasDefaultList(Params params) throws JsonProcessingException {
        return resourceQuotasService.getResourceQuotasTemplateList(params);
    }


}
