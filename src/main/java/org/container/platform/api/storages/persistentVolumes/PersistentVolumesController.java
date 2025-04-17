package org.container.platform.api.storages.persistentVolumes;

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
 * PersistentVolumes Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Tag(name = "PersistentVolumesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/persistentVolumes")
public class PersistentVolumesController {

    private final PersistentVolumesService persistentVolumesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new PersistentVolumes controller
     *
     * @param persistentVolumesService the persistentVolumes service
     */
    @Autowired
    public PersistentVolumesController(PersistentVolumesService persistentVolumesService, ResultStatusService resultStatusService) {
        this.persistentVolumesService = persistentVolumesService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * PersistentVolumes 목록 조회(Get PersistentVolumes list)
     *
     * @param params the params
     * @return the persistentVolumes list
     */
    @Operation(summary = "PersistentVolumes 목록 조회(Get PersistentVolumes list)", operationId = "getPersistentVolumesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public PersistentVolumesList getPersistentVolumesList(Params params) {
        return persistentVolumesService.getPersistentVolumesList(params);
    }


    /**
     * PersistentVolumes 상세 조회(Get PersistentVolumes detail)
     *
     * @param params the params
     * @return the persistentVolumes detail
     */
    @Operation(summary = "PersistentVolumes 상세 조회(Get PersistentVolumes detail)", operationId = "getPersistentVolumes")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public PersistentVolumes getPersistentVolumes(Params params) {

        return persistentVolumesService.getPersistentVolumes(params);
    }

    /**
     * PersistentVolumes YAML 조회(Get PersistentVolumes yaml)
     *
     * @param params the params
     * @return the persistentVolumes yaml
     */
    @Operation(summary = "PersistentVolumes YAML 조회(Get PersistentVolumes yaml)", operationId = "getPersistentVolumesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "{resourceName:.+}/yaml")
    public CommonResourcesYaml getPersistentVolumesYaml(Params params) {
        return persistentVolumesService.getPersistentVolumesYaml(params);
    }

    /**
     * PersistentVolumes 생성(Create PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumes 생성(Create PersistentVolumes)", operationId = "createPersistentVolumes")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createPersistentVolumes(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return persistentVolumesService.createPersistentVolumes(params);
    }

    /**
     * PersistentVolumes 삭제(Delete PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumes 삭제(Delete PersistentVolumes)", operationId = "deletePersistentVolumes")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deletePersistentVolumes(Params params) {
        return persistentVolumesService.deletePersistentVolumes(params);
    }

    /**
     * PersistentVolumes 수정(Update PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumes 수정(Update PersistentVolumes)", operationId = "updatePersistentVolumes")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePersistentVolumes(@RequestBody Params params) {
        return persistentVolumesService.updatePersistentVolumes(params);
    }
}
