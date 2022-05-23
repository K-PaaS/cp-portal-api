package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
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
 * ResourceQuotas Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 **/
@Api(value = "ResourceQuotasController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/resourceQuotas")
public class ResourceQuotasController {
    private final ResourceQuotasService resourceQuotasService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a ResourceQuotas Controller
     *
     * @param resourceQuotasService the resourceQuotas Service
     */
    @Autowired
    public ResourceQuotasController(ResourceQuotasService resourceQuotasService, ResultStatusService resultStatusService) {
        this.resourceQuotasService = resourceQuotasService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * ResourceQuotas 목록 조회(Get ResourceQuotas list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the resourceQuotas list
     */
    @ApiOperation(value = "ResourceQuotas 목록 조회(Get ResourceQuotas list)", nickname = "getResourceQuotasList")
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
    public Object getResourceQuotasList(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "0") int limit,
                                        @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                        @RequestParam(required = false, defaultValue = "") String order,
                                        @RequestParam(required = false, defaultValue = "") String searchName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return resourceQuotasService.getResourceQuotasListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return resourceQuotasService.getResourceQuotasListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return resourceQuotasService.getResourceQuotasList(namespace, offset, limit, orderBy, order, searchName);
    }

    /**
     * ResourceQuotas 상세 조회(Get ResourceQuotas detail)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the resourceQuotas detail
     */
    @ApiOperation(value = "ResourceQuotas 상세 조회(Get ResourceQuotas detail)", nickname = "getResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getResourceQuotas(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @PathVariable(value = "resourceName") String resourceName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return resourceQuotasService.getResourceQuotasAdmin(namespace, resourceName);
        }

        return resourceQuotasService.getResourceQuotas(namespace, resourceName);
    }


    /**
     * ResourceQuotas YAML 조회(Get ResourceQuotas yaml)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the resourceQuotas yaml
     */
    @ApiOperation(value = "ResourceQuotas YAML 조회(Get ResourceQuotas yaml)", nickname = "getResourceQuotasYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "{resourceName:.+}/yaml")
    public Object getResourceQuotasYaml(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "resourceName") String resourceName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return resourceQuotasService.getResourceQuotasAdminYaml(namespace, resourceName, new HashMap<>());
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * ResourceQuotas 생성(Create ResourceQuotas)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 생성(Create ResourceQuotas)", nickname = "createResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createResourceQuotas(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                       @RequestBody String yaml) throws Exception {
        if (isAdmin) {

            if (yaml.contains("---")) {
                Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, true);
                return object;
            }

            return resourceQuotasService.createResourceQuotas(namespace, yaml, true);
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * ResourceQuotas 삭제(Delete ResourceQuotas)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 삭제(Delete ResourceQuotas)", nickname = "deleteResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping(value = "/{resourceName:.+}")
    public ResultStatus deleteResourceQuotas(@PathVariable(value = "cluster") String cluster,
                                             @PathVariable(value = "namespace") String namespace,
                                             @PathVariable(value = "resourceName") String resourceName,
                                             @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return resourceQuotasService.deleteResourceQuotas(namespace, resourceName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * ResourceQuotas 수정(Update ResourceQuotas)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @param yaml         the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "ResourceQuotas 수정(Update ResourceQuotas)", nickname = "updateResourceQuotas")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping(value = "/{resourceName:.+}")
    public ResultStatus updateResourceQuotas(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @PathVariable(value = "resourceName") String resourceName,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                       @RequestBody String yaml) {
        if (isAdmin) {
            return resourceQuotasService.updateResourceQuotas(namespace, resourceName, yaml);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the resourceQuota list
     * @throws JsonProcessingException
     */
    @ApiOperation(value = "ResourceQuotas Default Template 목록 조회 (Get ResourceQuotas Default Template list)", nickname = "getResourceQuotasDefaultList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/template")
    public Object getResourceQuotasDefaultList(@PathVariable(value = "cluster") String cluster,
                                               @PathVariable(value = "namespace") String namespace,
                                               @RequestParam(required = false, defaultValue = "0") int offset,
                                               @RequestParam(required = false, defaultValue = "0") int limit,
                                               @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                               @RequestParam(required = false, defaultValue = "") String order,
                                               @RequestParam(required = false, defaultValue = "") String searchName,
                                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws JsonProcessingException {

        if (isAdmin) {
            return resourceQuotasService.getRqDefaultList(namespace, offset, limit, orderBy, order, searchName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


}
