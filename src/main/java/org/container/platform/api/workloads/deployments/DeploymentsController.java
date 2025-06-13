package org.container.platform.api.workloads.deployments;

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
 * Deployments Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.20
 */
@Tag(name = "DeploymentsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/deployments")
public class DeploymentsController {

    private final DeploymentsService deploymentsService;

    /**
     * Instantiates a new Deployments controller
     *
     * @param deploymentsService the deployments service
     */
    @Autowired
    public DeploymentsController(DeploymentsService deploymentsService) {
        this.deploymentsService = deploymentsService;
    }

    /**
     * Deployments 목록 조회(Get Deployments List)
     *
     * @param params the params
     * @return the deployments list
     */
    @Operation(summary = "Deployments 목록 조회(Get Deployments List)", operationId = "getDeploymentsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public DeploymentsList getDeploymentsList(Params params) {
        return deploymentsService.getDeploymentsList(params);
    }

    /**
     * Deployments 상세 조회(Get Deployments Detail)
     *
     * @param params the params
     * @return the deployments detail
     */
    @Operation(summary = "Deployments 상세 조회(Get Deployments Detail)", operationId = "getDeployments")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Deployments getDeployments(Params params) {
        return deploymentsService.getDeployments(params);
    }

    /**
     * Deployments YAML 조회(Get Deployments Yaml)
     *
     * @param params the params
     * @return the deployments yaml
     */
    @Operation(summary = "Deployments YAML 조회(Get Deployments Yaml)", operationId = "getDeploymentsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getDeploymentsYaml(Params params) {
        return deploymentsService.getDeploymentsYaml(params);
    }

    /**
     * Deployments 생성(Create Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Deployments 생성(Create Deployments)", operationId = "createDeployments")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createDeployments(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return deploymentsService.createDeployments(params);
    }

    /**
     * Deployments 수정(Update Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Deployments 수정(Update Deployments)", operationId = "updateDeployments")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateDeployments(@RequestBody Params params) {
        return deploymentsService.updateDeployments(params);
    }

    /**
     * Deployments 삭제(Delete Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Deployments 삭제(Delete Deployments)", operationId = "deleteDeployments")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteDeployments(Params params) {
        return deploymentsService.deleteDeployments(params);
    }

}