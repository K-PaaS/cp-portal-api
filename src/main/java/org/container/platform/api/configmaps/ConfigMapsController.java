package org.container.platform.api.configmaps;

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
 * ConfigMaps Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.25
 **/
@Tag(name = "ConfigMapsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/configMaps")
public class ConfigMapsController {

    private final ConfigMapsService configMapsService;


    /**
     * Instantiates a new ConfigMaps service
     *
     * @param configMapsService the ConfigMaps service
     */
    @Autowired
    public ConfigMapsController(ConfigMapsService configMapsService){
        this.configMapsService = configMapsService;
    }


    /**
     * ConfigMaps 리스트 조회(Get ConfigMaps List)
     *
     * @param params the params
     * @return the ConfigMaps detail
     */
    @Operation(summary = "ConfigMaps 목록 조회(Get ConfigMaps List)", operationId = "getConfigMapsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public ConfigMapsList getConfigMapsList(Params params) {
        return configMapsService.getConfigMapsList(params);
    }


    /**
     * ConfigMaps 상세 조회(Get ConfigMaps Detail)
     *
     * @param params the params
     * @return the ConfigMaps detail
     */
    @Operation(summary = "ConfigMaps 상세 조회(Get ConfigMaps Detail)", operationId = "getConfigMaps")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Object getConfigMaps(Params params) {
        return configMapsService.getConfigMaps(params);
    }


    /**
     * ConfigMaps YAML 조회(Get ConfigMaps YAML)
     *
     * @param params the params
     * @return the ConfigMaps yaml
     */
    @Operation(summary = "ConfigMaps YAML 조회(Get ConfigMaps yaml)", operationId = "getConfigMapsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getConfigMapsYaml(Params params) {
        return configMapsService.getConfigMapsYaml(params);
    }


    /**
     * ConfigMaps 생성(Create ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "ConfigMaps 생성(Create ConfigMaps)", operationId = "createConfigMaps")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createConfigMaps(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return configMapsService.createConfigMaps(params);
    }


    /**
     * ConfigMaps 삭제(Delete ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "ConfigMaps 삭제(Delete ConfigMaps)", operationId = "deleteConfigMaps")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteConfigMaps(Params params) {
        return configMapsService.deleteConfigMaps(params);
    }


    /**
     * ConfigMaps 수정(Update ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "ConfigMaps 수정(Update ConfigMaps)", operationId = "updateConfigMaps")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateConfigMaps(@RequestBody Params params) {
        return configMapsService.updateConfigMaps(params);
    }
}
