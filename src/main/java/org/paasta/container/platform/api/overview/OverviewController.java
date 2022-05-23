package org.paasta.container.platform.api.overview;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Overview Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.30
 **/
@Api(value = "OverviewController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/overview")
public class OverviewController {

    private final OverviewService overviewService;

    /**
     * Instantiates a new Overview controller
     *
     * @param overviewService the overview service
     */
    @Autowired
    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }


    /**
     * Overview 조회(Get Overview)
     *
     * @param cluster the cluster
     * @param namespace the namespace
     * @param isAdmin the isAdmin
     * @return the overview
     */
    @ApiOperation(value = "Overview 조회(Get Overview)", nickname = "getOverview")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping
    public Object getOverview(@PathVariable(value = "cluster") String cluster,
                              @PathVariable(value = "namespace") String namespace,
                              @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if(isAdmin) {
            if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
                return overviewService.getOverviewAll(cluster);
            }
            return overviewService.getOverview(cluster, namespace);
        }

        return overviewService.getOverview(cluster, namespace);
    }

}
