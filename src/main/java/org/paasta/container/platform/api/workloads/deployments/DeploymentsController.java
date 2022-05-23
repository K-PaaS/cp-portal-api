package org.paasta.container.platform.api.workloads.deployments;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * Deployments Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.08
 */
@Api(value = "DeploymentsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/deployments")
public class DeploymentsController {

    private final DeploymentsService deploymentsService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Deployments controller
     *
     * @param deploymentsService the deployments service
     */
    @Autowired
    public DeploymentsController(DeploymentsService deploymentsService, ResultStatusService resultStatusService) {
        this.deploymentsService = deploymentsService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * Deployments 목록 조회(Get Deployments list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the deployments list
     */
    @ApiOperation(value = "Deployments 목록 조회(Get Deployments list)", nickname = "getDeploymentsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping
    public Object getDeploymentsList(@PathVariable(value = "cluster") String cluster,
                                     @PathVariable(value = "namespace") String namespace,
                                     @RequestParam(required = false, defaultValue = "0") int offset,
                                     @RequestParam(required = false, defaultValue = "0") int limit,
                                     @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                     @RequestParam(required = false, defaultValue = "") String order,
                                     @RequestParam(required = false, defaultValue = "") String searchName,
                                     @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return deploymentsService.getDeploymentsListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return deploymentsService.getDeploymentsListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return deploymentsService.getDeploymentsList(namespace, offset, limit, orderBy, order, searchName);
    }

    /**
     * Deployments 상세 조회(Get Deployments detail)
     * (User Portal)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the deployments detail
     */
    @ApiOperation(value = "Deployments 상세 조회(Get Deployments detail)", nickname = "getDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getDeployments(@PathVariable(value = "namespace") String namespace,
                                 @PathVariable(value = "resourceName") String resourceName,
                                 @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // For Admin
        if (isAdmin) {
            return deploymentsService.getDeploymentsAdmin(namespace, resourceName);
        }

        return deploymentsService.getDeployments(namespace, resourceName);
    }


    /**
     * Deployments YAML 조회(Get Deployments yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the deployments yaml
     */
    @ApiOperation(value = "Deployments YAML 조회(Get Deployments yaml)", nickname = "getDeploymentsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getDeploymentsYaml(@PathVariable(value = "namespace") String namespace,
                                     @PathVariable(value = "resourceName") String resourceName,
                                     @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        // For Admin
        if (isAdmin) {
            return deploymentsService.getDeploymentsAdminYaml(namespace, resourceName, new HashMap<>());
        }

        return deploymentsService.getDeploymentsYaml(namespace, resourceName, new HashMap<>());
    }

    /**
     * Deployments 생성(Create Deployments)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "Deployments 생성(Create Deployments)", nickname = "createDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createDeployments(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @RequestBody String yaml,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, isAdmin);
            return object;
        }

        return deploymentsService.createDeployments(namespace, yaml, isAdmin);

    }

    /**
     * Deployments 삭제(Delete Deployments)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Deployments 삭제(Delete Deployments)", nickname = "deleteDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteDeployments(@PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        return deploymentsService.deleteDeployments(namespace, resourceName, isAdmin);
    }

    /**
     * Deployments 수정(Update Deployments)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Deployments 수정(Update Deployments)", nickname = "updateDeployments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateDeployments(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @RequestBody String yaml,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        return deploymentsService.updateDeployments(namespace, resourceName, yaml, isAdmin);
    }


}
