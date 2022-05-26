package org.paasta.container.platform.api.clusters.nodes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * Nodes Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
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
     * @param params the params
     * @return the nodes list
     */
    @ApiOperation(value = "Nodes 목록 조회(Get Nodes list)", nickname = "getNodesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public NodesList getNodesList(Params params) {
            return nodesService.getNodesList(params);
    }


    /**
     * Nodes 상세 조회(Get Nodes detail)
     *
     * @param params the params
     * @return the nodes detail
     */
    @ApiOperation(value = "Nodes 상세 조회(Get Nodes detail)", nickname = "getNodes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Nodes getNodes(Params params) {
            return nodesService.getNodes(params);
    }

}
