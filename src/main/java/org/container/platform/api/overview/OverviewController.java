package org.container.platform.api.overview;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Overview Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 **/
@Tag(name = "OverviewController v1")
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
    @Operation(summary = "Overview 조회(Get Overview)", operationId = "getOverview")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Global Overview 조회(Get Global Overview)", operationId = "getGlobalOverview")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping("/global/overview")
    public GlobalOverview getGlobalOverview(Params params) {
        return globalOverviewService.getGlobalOverview(params);
    }


}