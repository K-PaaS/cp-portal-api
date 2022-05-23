package org.paasta.container.platform.api.configmaps;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/configMaps")
public class ConfigMapsController {

    private final ConfigMapsService configMapsService;
    private final ResultStatusService resultStatusService;

    @Autowired
    public ConfigMapsController(ConfigMapsService configMapsService, ResultStatusService resultStatusService){
        this.configMapsService = configMapsService;
        this.resultStatusService = resultStatusService;
    }

    @GetMapping
    public Object getConfigMapsList(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @RequestParam(required = false, defaultValue = "0") int offset,
                                    @RequestParam(required = false, defaultValue = "0") int limit,
                                    @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                    @RequestParam(required = false, defaultValue = "") String order,
                                    @RequestParam(required = false, defaultValue = "") String searchName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return configMapsService.getConfigMapsListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }
        return configMapsService.getConfigMapsList(namespace, offset, limit, orderBy, order, searchName);
    }

    @GetMapping(value = "/{resourceName:.+}")
    public Object getConfigMaps(@PathVariable(value = "namespace") String namespace,
                                 @PathVariable(value = "resourceName") String resourceName,
                                 @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        return configMapsService.getConfigMaps(namespace, resourceName);
    }

    @ApiOperation(value = "Deployments YAML 조회(Get Deployments yaml)", nickname = "getDeploymentsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getConfigMapsYaml(@PathVariable(value = "namespace") String namespace,
                                     @PathVariable(value = "resourceName") String resourceName) {
        return configMapsService.getConfigMapsYaml(namespace, resourceName, new HashMap<>());
    }

    @PostMapping
    public Object createConfigMaps(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @RequestBody String yaml,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, isAdmin);
            return object;
        }
        return configMapsService.createConfigMaps(namespace, yaml, isAdmin);
    }

    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteConfigMaps(@PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        return configMapsService.deleteConfigMaps(namespace, resourceName, isAdmin);
    }

    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateConfigMaps(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @RequestBody String yaml,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        return configMapsService.updateConfigMaps(namespace, resourceName, yaml, isAdmin);
    }
}
