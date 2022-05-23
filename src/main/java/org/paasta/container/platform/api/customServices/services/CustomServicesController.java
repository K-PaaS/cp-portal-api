package org.paasta.container.platform.api.customServices.services;

import io.swagger.annotations.*;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * CustomServices Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.10
 */
@Api(value = "CustomServicesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/services")
public class CustomServicesController {

    private final CustomServicesService customServicesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new CustomServices controller
     *
     * @param customServicesService the customServices service
     */
    @Autowired
    public CustomServicesController(CustomServicesService customServicesService, ResultStatusService resultStatusService) {
        this.customServicesService = customServicesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Services 목록 조회(Get Services list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the services list
     */
    @ApiOperation(value = "Services 목록 조회(Get Services list)", nickname = "getCustomServicesList")
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
    public Object getCustomServicesList(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "0") int limit,
                                        @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                        @RequestParam(required = false, defaultValue = "") String order,
                                        @RequestParam(required = false, defaultValue = "") String searchName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return customServicesService.getCustomServicesListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return customServicesService.getCustomServicesListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return customServicesService.getCustomServicesList(namespace, offset, limit, orderBy, order, searchName);
    }


    /**
     * Services 상세 조회(Get Services detail)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the services detail
     */
    @ApiOperation(value = "Services 상세 조회(Get Services detail)", nickname = "getCustomServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getCustomServices(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @PathVariable(value = "resourceName") String resourceName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return customServicesService.getCustomServicesAdmin(namespace, resourceName);
        }

        return customServicesService.getCustomServices(namespace, resourceName);
    }


    /**
     * Services YAML 조회(Get Services yaml)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return the services yaml
     */
    @ApiOperation(value = "Services YAML 조회(Get Services yaml)", nickname = "getCustomServicesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getCustomServicesYaml(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "resourceName") String resourceName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return customServicesService.getCustomServicesAdminYaml(namespace, resourceName, new HashMap<>());
        }

        return customServicesService.getCustomServicesYaml(namespace, resourceName, new HashMap<>());
    }


    /**
     * Services 생성(Create Services)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */

    @ApiOperation(value = "Services 생성(Create Services)", nickname = "createServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createServices(@PathVariable(value = "cluster") String cluster,
                                 @PathVariable(value = "namespace") String namespace,
                                 @RequestBody String yaml,
                                 @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, isAdmin);
            return object;
        }

        return customServicesService.createServices(namespace, yaml, isAdmin);
    }


    /**
     * Services 삭제(Delete Services)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */

    @ApiOperation(value = "Services 삭제(Delete Services)", nickname = "deleteServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteServices(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @PathVariable(value = "resourceName") String resourceName,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return customServicesService.deleteServices(namespace, resourceName, isAdmin);
    }


    /**
     * Services 수정(Update Services)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Services 수정(Update Services)", nickname = "updateServices")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateServices(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @PathVariable(value = "resourceName") String resourceName,
                                       @RequestBody String yaml,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return customServicesService.updateServices(namespace, resourceName, yaml, isAdmin);
    }

}
