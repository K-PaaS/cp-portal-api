package org.container.platform.api.clusters.limitRanges;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * @param params the params
     * @return the limitRanges list
     */
    @ApiOperation(value = "LimitRanges 목록 조회(Get LimitRanges list)", nickname = "getLimitRangesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges 상세 조회(Get LimitRanges detail)", nickname = "getLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges YAML 조회(Get LimitRanges yaml)", nickname = "getLimitRangesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges 생성(Create LimitRanges)", nickname = "createLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges 삭제(Delete LimitRanges)", nickname = "deleteLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges 수정(Update LimitRanges)", nickname = "updateLimitRanges")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "LimitRanges Template 목록 조회(Get LimitRanges Template list)", nickname = "getLimitRangesTemplateList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/template")
    public Object getLimitRangesTemplateList(Params params) {

        return limitRangesService.getLimitRangesTemplateList(params);
    }
}
