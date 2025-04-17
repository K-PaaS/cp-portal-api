package org.container.platform.api.clusters.nodes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Nodes Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Tag(name = "NodesController v1")
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
    @Operation(summary = "Nodes 목록 조회(Get Nodes list)", operationId = "getNodesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Nodes 상세 조회(Get Nodes detail)", operationId = "getNodes")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Nodes getNodes(Params params) {
            return nodesService.getNodes(params);
    }

}
