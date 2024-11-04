package org.container.platform.api.workloads.deployments;

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
 * Deployments Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.20
 */
@Api(value = "DeploymentsController v1")
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
    @ApiOperation(value = "Deployments 목록 조회(Get Deployments List)", nickname = "getDeploymentsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping
    public DeploymentsList getDeploymentsList(Params params) {
        return deploymentsService.getDeploymentsList(params);
    }

    /**
     * Deployments Vault Secret 적용 목록 조회(Get Deployments Vault Secret List)
     *
     * @param params the params
     * @return the deployments list
     */
    @ApiOperation(value = "Deployments Vault Secret 목록 조회(Get Deployments List)", nickname = "getDeploymentsVaultSecretList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/vaultSecret")
    public DeploymentsList getDeploymentsVaultSecretList(Params params) {
        return deploymentsService.getDeploymentsVaultSecretList(params);
    }

    /**
     * Deployments 상세 조회(Get Deployments Detail)
     *
     * @param params the params
     * @return the deployments detail
     */
    @ApiOperation(value = "Deployments 상세 조회(Get Deployments Detail)", nickname = "getDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Deployments YAML 조회(Get Deployments Yaml)", nickname = "getDeploymentsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Deployments 생성(Create Deployments)", nickname = "createDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Deployments 수정(Update Deployments)", nickname = "updateDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Deployments 삭제(Delete Deployments)", nickname = "deleteDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteDeployments(Params params) {
        return deploymentsService.deleteDeployments(params);
    }


}