package org.container.platform.api.workloads.replicaSets;

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
 * ReplicaSets Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.23
 */
@Tag(name = "ReplicaSetsController v1")
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
    @Operation(summary = "ReplicaSets 목록 조회(Get ReplicaSets List)", operationId = "getReplicaSetsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ReplicaSets 상세 조회(Get ReplicaSets Detail)", operationId = "getReplicaSets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Selector 값에 따른 ReplicaSets 목록 조회(Get ReplicaSets By Selector)", operationId = "getReplicaSetsListLabelSelector")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ReplicaSets YAML 조회(Get ReplicaSets Yaml)", operationId = "getReplicaSetsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ReplicaSets 생성(Create ReplicaSets)", operationId = "createReplicaSets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ReplicaSets 수정(Update ReplicaSets)", operationId = "updateReplicaSets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "ReplicaSets 삭제(Delete ReplicaSets)", operationId = "deleteReplicaSets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping(value = "/{resourceName:.+}")
    public ResultStatus deleteReplicaSets(Params params) {
        return replicaSetsService.deleteReplicaSets(params);
    }


}