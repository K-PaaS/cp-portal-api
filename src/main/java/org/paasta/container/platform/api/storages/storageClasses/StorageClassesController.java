package org.paasta.container.platform.api.storages.storageClasses;

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
 * StorageClasses Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Api(value = "StorageClassesController v1")
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
    @ApiOperation(value = "StorageClasses 목록 조회(Get StorageClasses list)", nickname = "getStorageClassesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "StorageClasses 상세 조회(Get StorageClasses detail)", nickname = "getStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "StorageClasses YAML 조회(Get StorageClasses yaml)", nickname = "getStorageClassesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "StorageClasses 생성(Create StorageClasses)", nickname = "createStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "StorageClasses 삭제(Delete StorageClasses)", nickname = "deleteStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "StorageClasses 수정(Update StorageClasses)", nickname = "updateStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateStorageClasses(@RequestBody Params params) {
        return storageClassesService.updateStorageClasses(params);
    }
}
