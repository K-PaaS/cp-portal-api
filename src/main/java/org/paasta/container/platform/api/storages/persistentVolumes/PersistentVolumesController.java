package org.paasta.container.platform.api.storages.persistentVolumes;


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
 * PersistentVolumes Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Api(value = "PersistentVolumesController v1")
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
    @ApiOperation(value = "PersistentVolumes 목록 조회(Get PersistentVolumes list)", nickname = "getPersistentVolumesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumes 상세 조회(Get PersistentVolumes detail)", nickname = "getPersistentVolumes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumes YAML 조회(Get PersistentVolumes yaml)", nickname = "getPersistentVolumesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumes 생성(Create PersistentVolumes)", nickname = "createPersistentVolumes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumes 삭제(Delete PersistentVolumes)", nickname = "deletePersistentVolumes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "PersistentVolumes 수정(Update PersistentVolumes)", nickname = "updatePersistentVolumes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePersistentVolumes(@RequestBody Params params) {
        return persistentVolumesService.updatePersistentVolumes(params);
    }
}
