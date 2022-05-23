package org.paasta.container.platform.api.clusters.nodes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * Nodes Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Api(value = "NodesController v1")
@RestController
@RequestMapping(value = "/clusters/{cluster:.+}/nodes")
public class NodesController {
    private final NodesService nodesService;

    /**
     * Instantiates a new Nodes controller
     *
     * @param nodesService the nodes service
     */
    @Autowired
    public NodesController(NodesService nodesService) {
        this.nodesService = nodesService;
    }

    /**
     * Nodes 목록 조회(Get Nodes list)
     *
     * @param cluster    the cluster
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the nodes list
     */
    @ApiOperation(value = "Nodes 목록 조회(Get Nodes list)", nickname = "getNodesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping
    public Object getNodesList(@PathVariable(value = "cluster") String cluster,
                               @RequestParam(required = false, defaultValue = "0") int offset,
                               @RequestParam(required = false, defaultValue = "0") int limit,
                               @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                               @RequestParam(required = false, defaultValue = "") String order,
                               @RequestParam(required = false, defaultValue = "") String searchName,
                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return nodesService.getNodesListAdmin(offset, limit, orderBy, order, searchName);
        }

        return nodesService.getNodesList(offset, limit, orderBy, order, searchName);
    }


    /**
     * Nodes 상세 조회(Get Nodes detail)
     *
     * @param cluster the cluster
     * @param resourceName the resource name
     * @param isAdmin the isAdmin
     * @return the nodes detail
     */
    @ApiOperation(value = "Nodes 상세 조회(Get Nodes detail)", nickname = "getNodes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명",  required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getNodes(@PathVariable(value = "cluster") String cluster,
                           @PathVariable(value = "resourceName") String resourceName,
                           @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return nodesService.getNodesAdmin(resourceName);
        }

        return nodesService.getNodes(resourceName);
    }

}
