package org.paasta.container.platform.api.overview;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
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
     * @param params the params
     * @return the overview
     */
    @ApiOperation(value = "Overview 조회(Get Overview)", nickname = "getOverview")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public Overview getOverview(Params params) {

        if (params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
            return overviewService.getOverviewAll(params);
        }

        return overviewService.getOverview(params);

    }

}