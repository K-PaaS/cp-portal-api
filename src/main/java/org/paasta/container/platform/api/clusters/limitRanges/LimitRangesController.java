package org.paasta.container.platform.api.clusters.limitRanges;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.ResourceExecuteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * LimitRanges Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.22
 */
@Api(value = "LimitRangesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/limitRanges")
public class LimitRangesController {

    private final LimitRangesService limitRangesService;
    private final ResultStatusService resultStatusService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitRangesController.class);

    /**
     * Instantiates a new LimitRanges controller
     *
     * @param limitRangesService the limitRanges service
     */
    @Autowired
    public LimitRangesController(LimitRangesService limitRangesService, ResultStatusService resultStatusService) {
        this.limitRangesService = limitRangesService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * LimitRanges 목록 조회(Get LimitRanges list)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the limitRanges list
     */
    @ApiOperation(value = "LimitRanges 목록 조회(Get LimitRanges list)", nickname = "getLimitRangesList")
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
    public Object getLimitRangesList(@PathVariable(value = "cluster") String cluster,
                                     @PathVariable(value = "namespace") String namespace,
                                     @RequestParam(required = false, defaultValue = "0") int offset,
                                     @RequestParam(required = false, defaultValue = "0") int limit,
                                     @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                     @RequestParam(required = false, defaultValue = "") String order,
                                     @RequestParam(required = false, defaultValue = "") String searchName,
                                     @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            if (isAdmin) {
                return limitRangesService.getLimitRangesListAllNamespacesAdmin(offset, limit, orderBy, order, searchName);
            } else {
                return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
            }
        }

        if (isAdmin) {
            return limitRangesService.getLimitRangesListAdmin(namespace, offset, limit, orderBy, order, searchName);
        }

        return limitRangesService.getLimitRangesList(namespace, offset, limit, orderBy, order, searchName);
    }


    /**
     * LimitRanges 상세 조회(Get LimitRanges detail)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the limitRanges detail
     */
    @ApiOperation(value = "LimitRanges 상세 조회(Get LimitRanges detail)", nickname = "getLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getLimitRanges(@PathVariable(value = "cluster") String cluster,
                                 @PathVariable(value = "namespace") String namespace,
                                 @PathVariable(value = "resourceName") String resourceName,
                                 @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return limitRangesService.getLimitRangesAdmin(namespace, resourceName);
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * LimitRanges YAML 조회(Get LimitRanges yaml)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the limitRanges yaml
     */
    @ApiOperation(value = "LimitRanges YAML 조회(Get LimitRanges yaml)", nickname = "getLimitRangesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "{resourceName:.+}/yaml")
    public Object getLimitRangesYaml(@PathVariable(value = "cluster") String cluster,
                                     @PathVariable(value = "namespace") String namespace,
                                     @PathVariable(value = "resourceName") String resourceName,
                                     @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return limitRangesService.getLimitRangesAdminYaml(namespace, resourceName, new HashMap<>());
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * LimitRanges 생성(Create LimitRanges)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @param yaml      the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "LimitRanges 생성(Create LimitRanges)", nickname = "createLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 생성 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PostMapping
    public Object createLimitRanges(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                    @RequestBody String yaml) throws Exception {

        if (isAdmin) {

            if (yaml.contains("---")) {
                Object object = ResourceExecuteManager.commonControllerExecute(namespace, yaml, true);
                return object;
            }

            return limitRangesService.createLimitRanges(namespace, yaml, true);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * LimitRanges 삭제(Delete LimitRanges)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "LimitRanges 삭제(Delete LimitRanges)", nickname = "deleteLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteLimitRanges(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return limitRangesService.deleteLimitRanges(namespace, resourceName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * LimitRanges 수정(Update LimitRanges)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @param yaml         the yaml
     * @return return is succeeded
     */
    @ApiOperation(value = "LimitRanges 수정(Update LimitRanges)", nickname = "updateLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "yaml", value = "리소스 수정 yaml", required = true, dataType = "string", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateLimitRanges(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "resourceName") String resourceName,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                          @RequestBody String yaml) {

        if (isAdmin) {
            return limitRangesService.updateLimitRanges(namespace, resourceName, yaml);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * LimitRanges Template 목록 조회(Get LimitRanges Template list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the limitRangesDefault list
     */
    @ApiOperation(value = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", nickname = "getLimitRangesTemplateList")
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
    public Object getLimitRangesTemplateList(@PathVariable(value = "cluster") String cluster,
                                             @PathVariable(value = "namespace") String namespace,
                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                             @RequestParam(required = false, defaultValue = "0") int limit,
                                             @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                             @RequestParam(required = false, defaultValue = "") String order,
                                             @RequestParam(required = false, defaultValue = "") String searchName,
                                             @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        return limitRangesService.getLimitRangesTemplateList(namespace, offset, limit, orderBy, order, searchName);
    }
}
