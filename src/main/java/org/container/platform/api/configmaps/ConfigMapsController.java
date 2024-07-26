package org.container.platform.api.configmaps;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "ConfigMapsController v1")
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
    @ApiOperation(value = "ConfigMaps 목록 조회(Get ConfigMaps List)", nickname = "getConfigMapsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "ConfigMaps 상세 조회(Get ConfigMaps Detail)", nickname = "getConfigMaps")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "ConfigMaps YAML 조회(Get ConfigMaps yaml)", nickname = "getConfigMapsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "ConfigMaps 생성(Create ConfigMaps)", nickname = "createConfigMaps")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "ConfigMaps 삭제(Delete ConfigMaps)", nickname = "deleteConfigMaps")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "ConfigMaps 수정(Update ConfigMaps)", nickname = "updateConfigMaps")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateConfigMaps(@RequestBody Params params) {
        return configMapsService.updateConfigMaps(params);
    }
}
