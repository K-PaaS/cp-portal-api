package org.container.platform.api.clusters.limitRanges;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * LimitRanges Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Tag(name = "LimitRangesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/limitRanges")
public class LimitRangesController {

    private final LimitRangesService limitRangesService;
    private final ResultStatusService resultStatusService;

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
     * @param params the params
     * @return the limitRanges list
     */
    @Operation(summary = "LimitRanges 목록 조회(Get LimitRanges list)", operationId = "getLimitRangesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public LimitRangesList getLimitRangesList(Params params) {

        if (params.getNamespace().toLowerCase().equals(Constants.ALL_NAMESPACES)) {
                return limitRangesService.getLimitRangesListAllNamespaces(params);
        }
            return limitRangesService.getLimitRangesList(params);
    }


    /**
     * LimitRanges 상세 조회(Get LimitRanges detail)
     *
     * @param params the params
     * @return the limitRanges detail
     */
    @Operation(summary = "LimitRanges 상세 조회(Get LimitRanges detail)", operationId = "getLimitRanges")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public LimitRanges getLimitRanges(Params params) {
            return limitRangesService.getLimitRanges(params);
    }

    /**
     * LimitRanges YAML 조회(Get LimitRanges yaml)
     *
     * @param params the params
     * @return the limitRanges yaml
     */
    @Operation(summary = "LimitRanges YAML 조회(Get LimitRanges yaml)", operationId = "getLimitRangesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "{resourceName:.+}/yaml")
    public Object getLimitRangesYaml(Params params) {
            return limitRangesService.getLimitRangesYaml(params, new HashMap<>());
    }

    /**
     * LimitRanges 생성(Create LimitRanges)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "LimitRanges 생성(Create LimitRanges)", operationId = "createLimitRanges")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createLimitRanges(@RequestBody Params params) throws Exception {
            if (params.getYaml().contains("---")) {
                Object object = ResourceExecuteManager.commonControllerExecute(params);
                return object;
            }
            return limitRangesService.createLimitRanges(params);
    }


    /**
     * LimitRanges 삭제(Delete LimitRanges)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "LimitRanges 삭제(Delete LimitRanges)", operationId = "deleteLimitRanges")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteLimitRanges(Params params) {
            return limitRangesService.deleteLimitRanges(params);
    }


    /**
     * LimitRanges 수정(Update LimitRanges)
     *
     * @param params the params
     * @return return is succeeded
     */
    @Operation(summary = "LimitRanges 수정(Update LimitRanges)", operationId = "updateLimitRanges")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateLimitRanges(@RequestBody Params params) {
            return limitRangesService.updateLimitRanges(params);
    }


    /**
     * LimitRanges Template 목록 조회(Get LimitRanges Template list)
     *
     * @param params the params
     * @return the limitRangesDefault list
     */
    @Operation(summary = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", operationId = "getLimitRangesTemplateList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/template")
    public Object getLimitRangesTemplateList(Params params) {

        return limitRangesService.getLimitRangesTemplateList(params);
    }
}
