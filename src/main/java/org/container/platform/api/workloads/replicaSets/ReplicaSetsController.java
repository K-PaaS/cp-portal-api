package org.container.platform.api.workloads.replicaSets;

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
 * ReplicaSets Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.23
 */
@Api(value = "ReplicaSetsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/replicaSets")
public class ReplicaSetsController {

    private final ReplicaSetsService replicaSetsService;

    /**
     * Instantiates a new ReplicaSets controller
     *
     * @param replicaSetsService the replicaSets service
     */
    @Autowired
    public ReplicaSetsController(ReplicaSetsService replicaSetsService) {
        this.replicaSetsService = replicaSetsService;
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets List)
     *
     * @param params the params
     * @return the replicaSets list
     */
    @ApiOperation(value = "ReplicaSets 목록 조회(Get ReplicaSets List)", nickname = "getReplicaSetsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public ReplicaSetsList getReplicaSetsList(Params params) {
        return replicaSetsService.getReplicaSetsList(params);
    }


    /**
     * ReplicaSets 상세 조회(Get ReplicaSets Detail)
     *
     * @param params the params
     * @return the replicaSets detail
     */
    @ApiOperation(value = "ReplicaSets 상세 조회(Get ReplicaSets Detail)", nickname = "getReplicaSets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public ReplicaSets getReplicaSets(Params params) {
        return replicaSetsService.getReplicaSets(params);
    }


    /**
     * Selector 값에 따른 ReplicaSets 목록 조회(Get ReplicaSets By Selector)
     *
     * @param params the params
     * @return the replicaSets list
     */
    @ApiOperation(value = "Selector 값에 따른 ReplicaSets 목록 조회(Get ReplicaSets By Selector)", nickname = "getReplicaSetsListLabelSelector")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/resources")
    public ReplicaSetsList getReplicaSetsListLabelSelector(Params params) {
        return replicaSetsService.getReplicaSetsListLabelSelector(params);
    }


    /**
     * ReplicaSets YAML 조회(Get ReplicaSets Yaml)
     *
     * @param params the params
     * @return the replicaSets yaml
     */
    @ApiOperation(value = "ReplicaSets YAML 조회(Get ReplicaSets Yaml)", nickname = "getReplicaSetsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getReplicaSetsYaml(Params params) {
        return replicaSetsService.getReplicaSetsYaml(params);
    }


    /**
     * ReplicaSets 생성(Create ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "ReplicaSets 생성(Create ReplicaSets)", nickname = "createReplicaSets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public Object createReplicaSets(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return replicaSetsService.createReplicaSets(params);
    }


    /**
     * ReplicaSets 수정(Update ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "ReplicaSets 수정(Update ReplicaSets)", nickname = "updateReplicaSets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/{resourceName:.+}")
    public ResultStatus updateReplicaSets(@RequestBody Params params) {
        return replicaSetsService.updateReplicaSets(params);
    }

    /**
     * ReplicaSets 삭제(Delete ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "ReplicaSets 삭제(Delete ReplicaSets)", nickname = "deleteReplicaSets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{resourceName:.+}")
    public ResultStatus deleteReplicaSets(Params params) {
        return replicaSetsService.deleteReplicaSets(params);
    }


}