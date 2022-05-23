package org.paasta.container.platform.api.storages.storageClasses;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * StorageClasses Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.13
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
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the storageClasses list
     */
    @ApiOperation(value = "StorageClasses 목록 조회(Get StorageClasses list)", nickname = "getStorageClassesList")
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
    public Object getStorageClassesList(@PathVariable(value = "cluster") String cluster,
                                        @PathVariable(value = "namespace") String namespace,
                                        @RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "0") int limit,
                                        @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                        @RequestParam(required = false, defaultValue = "") String order,
                                        @RequestParam(required = false, defaultValue = "") String searchName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return storageClassesService.getStorageClassesListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * StorageClasses 상세 조회(Get StorageClasses detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the storageClasses detail
     */
    @ApiOperation(value = "StorageClasses 상세 조회(Get StorageClasses detail)", nickname = "getStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getStorageClasses(@PathVariable(value = "namespace") String namespace,
                                    @PathVariable(value = "resourceName") String resourceName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // For Admin
        if (isAdmin) {
            return storageClassesService.getStorageClassesAdmin(namespace, resourceName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * StorageClasses YAML 조회(Get StorageClasses yaml)
     *
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the storageClasses yaml
     */
    @ApiOperation(value = "StorageClasses YAML 조회(Get StorageClasses yaml)", nickname = "getStorageClassesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getStorageClassesYaml(@PathVariable(value = "resourceName") String resourceName,
                                        @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return storageClassesService.getStorageClassesAdminYaml(resourceName, new HashMap<>());
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * StorageClasses 생성(Create StorageClasses)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @param yaml      the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "StorageClasses 생성(Create StorageClasses)", nickname = "createStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createStorageClasses(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                       @RequestBody String yaml) throws Exception {
        if (isAdmin) {

            if (yaml.contains("---")) {
                Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, true);
                return object;
            }

            return storageClassesService.createStorageClasses(namespace, yaml, true);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * StorageClasses 삭제(Delete StorageClasses)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "StorageClasses 삭제(Delete StorageClasses)", nickname = "deleteStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteStorageClasses(@PathVariable(value = "cluster") String cluster,
                                             @PathVariable(value = "namespace") String namespace,
                                             @PathVariable(value = "resourceName") String resourceName,
                                             @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return storageClassesService.deleteStorageClasses(namespace, resourceName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * StorageClasses 수정(Update StorageClasses)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @param yaml         the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "StorageClasses 수정(Update StorageClasses)", nickname = "updateStorageClasses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateStorageClasses(@PathVariable(value = "cluster") String cluster,
                                       @PathVariable(value = "namespace") String namespace,
                                       @PathVariable(value = "resourceName") String resourceName,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                       @RequestBody String yaml) {

        if (isAdmin) {
            return storageClassesService.updateStorageClasses(resourceName, yaml);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }
}
