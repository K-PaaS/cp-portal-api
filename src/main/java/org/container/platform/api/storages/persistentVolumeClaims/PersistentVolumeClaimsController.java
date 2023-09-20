package org.container.platform.api.storages.persistentVolumeClaims;

import io.swagger.annotations.*;
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
@Api(value = "PersistentVolumeClaimsController v1")
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
    @ApiOperation(value = "PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)", nickname = "getPersistentVolumeClaimsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)", nickname = "getPersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)", nickname = "getPersistentVolumeClaimsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumeClaims 생성(Create PersistentVolumeClaims)", nickname = "createPersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)", nickname = "deletePersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumeClaims 수정(Update PersistentVolumeClaims)", nickname = "updatePersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePersistentVolumeClaims(@RequestBody Params params) {

        return persistentVolumeClaimsService.updatePersistentVolumeClaims(params);
    }

}
