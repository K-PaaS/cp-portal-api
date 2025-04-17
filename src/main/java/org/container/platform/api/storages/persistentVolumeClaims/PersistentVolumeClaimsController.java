package org.container.platform.api.storages.persistentVolumeClaims;

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
 * PersistentVolumeClaims Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Tag(name = "PersistentVolumeClaimsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/persistentVolumeClaims")
public class PersistentVolumeClaimsController {

    private final PersistentVolumeClaimsService persistentVolumeClaimsService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new PersistentVolumeClaims controller
     *
     * @param persistentVolumeClaimsService the persistentVolumeClaims service
     */
    @Autowired
    public PersistentVolumeClaimsController(PersistentVolumeClaimsService persistentVolumeClaimsService, ResultStatusService resultStatusService) {
        this.persistentVolumeClaimsService = persistentVolumeClaimsService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)
     *
     * @param params the params
     * @return the persistentVolumeClaims list
     */
    @Operation(summary = "PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)", operationId = "getPersistentVolumeClaimsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @GetMapping
    public PersistentVolumeClaimsList getPersistentVolumeClaimsList(Params params) {
        return persistentVolumeClaimsService.getPersistentVolumeClaimsList(params);
    }

    /**
     * PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)
     *
     * @param params the params
     * @return the persistentVolumeClaims detail
     */
    @Operation(summary = "PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)", operationId = "getPersistentVolumeClaims")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public PersistentVolumeClaims getPersistentVolumeClaims(Params params) {
        return persistentVolumeClaimsService.getPersistentVolumeClaims(params);
    }

    /**
     * PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)
     *
     * @param params the params
     * @return the persistentVolumeClaims yaml
     */
    @Operation(summary = "PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)", operationId = "getPersistentVolumeClaimsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getPersistentVolumeClaimsYaml(Params params) {
        return persistentVolumeClaimsService.getPersistentVolumeClaimsYaml(params);
    }

    /**
     * PersistentVolumeClaims 생성(Create PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumeClaims 생성(Create PersistentVolumeClaims)", operationId = "createPersistentVolumeClaims")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @PostMapping
    public Object createPersistentVolumeClaims(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }

        return persistentVolumeClaimsService.createPersistentVolumeClaims(params);
    }

    /**
     * PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)", operationId = "deletePersistentVolumeClaims")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deletePersistentVolumeClaims(Params params) {
        return persistentVolumeClaimsService.deletePersistentVolumeClaims(params);
    }

    /**
     * PersistentVolumeClaims 수정(Update PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "PersistentVolumeClaims 수정(Update PersistentVolumeClaims)", operationId = "updatePersistentVolumeClaims")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePersistentVolumeClaims(@RequestBody Params params) {
        return persistentVolumeClaimsService.updatePersistentVolumeClaims(params);
    }

}
