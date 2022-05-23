package org.paasta.container.platform.api.customServices.ingresses;

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
 * Ingresses Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.17
 */
@Api(value = "IngressesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/ingresses")
public class IngressesController {

    private final IngressesService ingressesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Ingresses controller
     *
     * @param ingressesService the ingresses service
     */
    @Autowired
    public IngressesController(IngressesService ingressesService, ResultStatusService resultStatusService){
        this.ingressesService = ingressesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Ingresses 목록 조회(Get Ingresses list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the ingresses list
     */
    @ApiOperation(value = "Ingresses 목록 조회(Get Ingresses list)", nickname = "getIngressesList")
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
    public Object getIngressesList(@PathVariable(value = "cluster") String cluster,
                                   @PathVariable(value = "namespace") String namespace,
                                   @RequestParam(required = false, defaultValue = "0") int offset,
                                   @RequestParam(required = false, defaultValue = "0") int limit,
                                   @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                   @RequestParam(required = false, defaultValue = "") String order,
                                   @RequestParam(required = false, defaultValue = "") String searchName,
                                   @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin){
        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return ingressesService.getIngressesListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return ingressesService.getIngressesListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return ingressesService.getIngressesList(namespace, offset, limit, orderBy, order, searchName);
    }


    /**
     * Ingresses 상세 조회(Get Ingresses detail)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the ingresses detail
     */
    @ApiOperation(value = "Ingresses 상세 조회(Get Ingresses detail)", nickname = "getIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getIngresses(@PathVariable(value = "cluster") String cluster,
                               @PathVariable(value = "namespace") String namespace,
                               @PathVariable(value = "resourceName") String resourceName,
                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin){
        if (isAdmin) {
            return ingressesService.getIngressesAdmin(namespace, resourceName);
        }

        return ingressesService.getIngresses(namespace, resourceName);
    }

    /**
     * Ingresses YAML 조회(Get Ingresses yaml)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the ingresses yaml
     */
    @ApiOperation(value = "Ingresses YAML 조회(Get Ingresses yaml)", nickname = "getIngressesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getIngressesYaml(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "resourceName") String resourceName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return ingressesService.getIngressesAdminYaml(namespace, resourceName, new HashMap<>());
        }

        return ingressesService.getIngressesYaml(namespace, resourceName, new HashMap<>());
    }

    /**
     * Ingresses 생성(Create Ingresses)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Ingresses 생성(Create Ingresses)", nickname = "createIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createIngresses(@PathVariable(value = "cluster") String cluster,
                                  @PathVariable(value = "namespace") String namespace,
                                  @RequestBody String yaml,
                                  @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, isAdmin);
            return object;
        }
        return ingressesService.createIngresses(namespace, yaml, isAdmin);
    }

    /**
     * Ingresses 삭제(Delete Ingresses)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Ingresses 삭제(Delete Ingresses)", nickname = "deleteIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteIngresses(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "resourceName") String resourceName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return ingressesService.deleteIngresses(namespace, resourceName, isAdmin);
    }


    /**
     * Ingresses 수정(Update Ingresses)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Ingresses 수정(Update Ingresses)", nickname = "updateIngresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateIngresses(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "resourceName") String resourceName,
                                        @RequestBody String yaml,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return ingressesService.updateIngresses(namespace, resourceName, yaml, isAdmin);
    }
    }
