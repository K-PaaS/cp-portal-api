package org.container.platform.api.catalog.chart;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Catalog Chart Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2024.05.17
 **/
@Api(value = "ChartController v1")
@RestController
@RequestMapping("/catalog/charts")
public class ChartController {
    private final ChartService chartService;


    /**
     * Instantiates a new Chart controller
     *
     * @param chartService the Chart service
     */
    @Autowired
    ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * 차트 버전 목록 조회(Get Chart Versions)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "차트 버전 목록 조회(Get Chart Versions)", nickname = "GetChartVersions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{charts:.+}/versions")
    public CatalogStatus getChartVersions(Params params) {
        return chartService.getChartVersions(params);
    }

}

