package org.paasta.container.platform.api.overview;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Overview Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 **/
@Api(value = "OverviewController v1")
@RestController
public class OverviewController {

    private final OverviewService overviewService;
    private final GlobalOverviewService globalOverviewService;

    /**
     * Instantiates a new Overview controller
     *
     * @param overviewService the overview service
     */
    @Autowired
    public OverviewController(OverviewService overviewService, GlobalOverviewService globalOverviewService) {
        this.overviewService = overviewService;
        this.globalOverviewService = globalOverviewService;
    }


    /**
     * Overview 조회(Get Overview)
     *
     * @param params the params
     * @return the overview
     */
    @ApiOperation(value = "Overview 조회(Get Overview)", nickname = "getOverview")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/overview")
    public Overview getOverview(Params params) {
        return overviewService.getOverviewAll(params);

    }


    /**
     * Global Overview 조회(Get Global Overview)
     *
     * @param params the params
     * @return the global overview
     */
    @ApiOperation(value = "Global Overview 조회(Get Global Overview)", nickname = "getGlobalOverview")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/global/overview")
    public GlobalOverview getGlobalOverview(Params params) {
        return globalOverviewService.getGlobalOverview(params);
    }


    /**
     * Global Overview 조회(Get Global Overview)
     *
     * @param params the params
     * @return the global overview
     */
    @ApiOperation(value = "Global Overview 조회(Get Global Overview)", nickname = "getGlobalOverview")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/test")
    public Object getTest(Params params) {

        return null;//return globalOverviewService.topPods(params);
    }
}