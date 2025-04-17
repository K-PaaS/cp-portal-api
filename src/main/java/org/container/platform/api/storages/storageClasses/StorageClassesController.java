package org.container.platform.api.storages.storageClasses;

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
 * StorageClasses Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Tag(name = "StorageClassesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/storageClasses")
public class StorageClassesController {
    private StorageClassesService storageClassesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new StorageClasses controller
     *
     * @param storageClassesService the storageClasses service
     */
    @Autowired
    public StorageClassesController(StorageClassesService storageClassesService, ResultStatusService resultStatusService) {
        this.storageClassesService = storageClassesService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * StorageClasses 목록 조회(Get StorageClasses list)
     *
     * @param params the params
     * @return the storageClasses list
     */
    @Operation(summary = "StorageClasses 목록 조회(Get StorageClasses list)", operationId = "getStorageClassesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public StorageClassesList getStorageClassesList(Params params) {
        return storageClassesService.getStorageClassesList(params);
    }

    /**
     * StorageClasses 상세 조회(Get StorageClasses detail)
     *
     * @param params the params
     * @return the storageClasses detail
     */
    @Operation(summary = "StorageClasses 상세 조회(Get StorageClasses detail)", operationId = "getStorageClasses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public StorageClasses getStorageClasses(Params params) {
        return storageClassesService.getStorageClasses(params);
    }

    /**
     * StorageClasses YAML 조회(Get StorageClasses yaml)
     *
     * @param params the params
     * @return the storageClasses yaml
     */
    @Operation(summary = "StorageClasses YAML 조회(Get StorageClasses yaml)", operationId = "getStorageClassesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getStorageClassesYaml(Params params) {
        return storageClassesService.getStorageClassesYaml(params);
    }

    /**
     * StorageClasses 생성(Create StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "StorageClasses 생성(Create StorageClasses)", operationId = "createStorageClasses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createStorageClasses(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return storageClassesService.createStorageClasses(params);
    }

    /**
     * StorageClasses 삭제(Delete StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "StorageClasses 삭제(Delete StorageClasses)", operationId = "deleteStorageClasses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteStorageClasses(Params params) {
        return storageClassesService.deleteStorageClasses(params);
    }

    /**
     * StorageClasses 수정(Update StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "StorageClasses 수정(Update StorageClasses)", operationId = "updateStorageClasses")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateStorageClasses(@RequestBody Params params) {
        return storageClassesService.updateStorageClasses(params);
    }
}
