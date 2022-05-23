package org.paasta.container.platform.api.storages.persistentVolumeClaims;

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
 * PersistentVolumeClaims Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.18
 */
@Api(value = "PersistentVolumeClaimsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/persistentVolumeClaims")
public class PersistentVolumeClaimsController {

    private final PersistentVolumeClaimsService persistentVolumeClaimsService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new PersistentVolumeClaims controller
     *
     * @param persistentVolumeClaimsService the persistentVolumeClaims service
     */
    @Autowired
    public PersistentVolumeClaimsController(PersistentVolumeClaimsService persistentVolumeClaimsService, ResultStatusService resultStatusService) {
        this.persistentVolumeClaimsService = persistentVolumeClaimsService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the persistentVolumeClaims list
     */
    @ApiOperation(value = "PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)", nickname = "getPersistentVolumeClaimsList")
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
    public Object getPersistentVolumeClaimsList(@PathVariable(value = "cluster") String cluster,
                                                @PathVariable(value = "namespace") String namespace,
                                                @RequestParam(required = false, defaultValue = "0") int offset,
                                                @RequestParam(required = false, defaultValue = "0") int limit,
                                                @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                                @RequestParam(required = false, defaultValue = "") String order,
                                                @RequestParam(required = false, defaultValue = "") String searchName,
                                                @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return persistentVolumeClaimsService.getPersistentVolumeClaimsListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return persistentVolumeClaimsService.getPersistentVolumeClaimsListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }
        return persistentVolumeClaimsService.getPersistentVolumeClaimsList(namespace, offset, limit, orderBy, order, searchName);
    }

    /**
     * PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the persistentVolumeClaims detail
     */
    @ApiOperation(value = "PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)", nickname = "getPersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getPersistentVolumeClaims(@PathVariable(value = "namespace") String namespace,
                                            @PathVariable(value = "resourceName") String resourceName,
                                            @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // For Admin
        if (isAdmin) {
            return persistentVolumeClaimsService.getPersistentVolumeClaimsAdmin(namespace, resourceName);
        }
        return persistentVolumeClaimsService.getPersistentVolumeClaims(namespace, resourceName);
    }

    /**
     * PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the persistentVolumeClaims yaml
     */
    @ApiOperation(value = "PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)", nickname = "getPersistentVolumeClaimsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public Object getPersistentVolumeClaimsYaml(@PathVariable(value = "namespace") String namespace,
                                                                    @PathVariable(value = "resourceName") String resourceName,
                                                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        // For Admin
        if (isAdmin) {
            return persistentVolumeClaimsService.getPersistentVolumeClaimsAdminYaml(namespace, resourceName, new HashMap<>());
        }

        return persistentVolumeClaimsService.getPersistentVolumeClaimsYaml(namespace, resourceName, new HashMap<>());
    }

    /**
     * PersistentVolumeClaims 생성(Create PersistentVolumeClaims)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "PersistentVolumeClaims 생성(Create PersistentVolumeClaims)", nickname = "createPersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createPersistentVolumeClaims(@PathVariable(value = "cluster") String cluster,
                                               @PathVariable(value = "namespace") String namespace,
                                               @RequestBody String yaml,
                                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) throws Exception {
        if (yaml.contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, isAdmin);
            return object;
        }

        return persistentVolumeClaimsService.createPersistentVolumeClaims(namespace, yaml, isAdmin);
    }

    /**
     * PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)", nickname = "deletePersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deletePersistentVolumeClaims(@PathVariable(value = "namespace") String namespace,
                                                     @PathVariable(value = "resourceName") String resourceName,
                                                     @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return persistentVolumeClaimsService.deletePersistentVolumeClaims(namespace, resourceName, isAdmin);
    }

    /**
     * PersistentVolumeClaims 수정(Update PersistentVolumeClaims)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "PersistentVolumeClaims 수정(Update PersistentVolumeClaims)", nickname = "updatePersistentVolumeClaims")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePersistentVolumeClaims(@PathVariable(value = "cluster") String cluster,
                                               @PathVariable(value = "namespace") String namespace,
                                               @PathVariable(value = "resourceName") String resourceName,
                                               @RequestBody String yaml,
                                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return persistentVolumeClaimsService.updatePersistentVolumeClaims(namespace, resourceName, yaml, isAdmin);
    }

}
