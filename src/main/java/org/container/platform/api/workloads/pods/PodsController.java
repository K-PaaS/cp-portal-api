package org.container.platform.api.workloads.pods;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Pods Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.20
 */
@Api(value = "PodsController v1")
@RestController
@RequestMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/pods")
public class PodsController {
    private final PodsService podsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PodsController.class);

    /**
     * Instantiates a new Pods controller
     *
     * @param podsService the pods service
     */
    @Autowired
    public PodsController(PodsService podsService) {
        this.podsService = podsService;
    }

    /**
     * Pods 목록 조회(Get Pods List)
     *
     * @param params the params
     * @return the pods list
     */
    @ApiOperation(value = "Pods 목록 조회(Get Pods List)", nickname = "getPodsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    @ResponseBody
    public PodsList getPodsList(Params params) {
        return podsService.getPodsList(params);

    }


    /**
     * Selector 값에 따른 Pods 목록 조회(Get Pods By Selector)
     *
     * @param params the params
     * @return the pods list
     */
    @ApiOperation(value = "Selector 값에 따른 Pods 목록 조회(Get Pods By Selector)", nickname = "getPodListBySelector")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/resources")
    @ResponseBody
    public PodsList getPodsListBySelector(Params params) {
            return podsService.getPodListWithLabelSelector(params);
    }


    /**
     * Node 명에 따른 Pods 목록 조회(Get Pods By Node)
     *
     * @param params the params
     * @return the pods list
     */
    @ApiOperation(value = "Node 명에 따른 Pods 목록 조회(Get Pods By Node)", nickname = "getPodListByNode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/nodes/{nodeName:.+}")
    public PodsList getPodsListByNode(Params params) {
            return podsService.getPodsListByNode(params);
    }

    /**
     * Pods 상세 조회(Get Pods Detail)
     *
     * @param params the params
     * @return the pods detail
     */
    @ApiOperation(value = "Pods 상세 조회(Get Pods Detail)", nickname = "getPods")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Pods getPods(Params params) {
        return podsService.getPods(params);
    }

    /**
     * Pods YAML 조회(Get Pods Yaml)
     *
     * @param params the params
     * @return the pods yaml
     */
    @ApiOperation(value = "Pods YAML 조회(Get Pods Yaml)", nickname = "getPodsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getPodsYaml(Params params) {
            return podsService.getPodsYaml(params);
    }

    /**
     * Pods 생성(Create Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Pods 생성(Create Pods)", nickname = "createPods")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public Object createPods(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return podsService.createPods(params);

    }

    /**
     * Pods 수정(Update Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Pods 수정(Update Pods)", nickname = "updatePods")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updatePods(@RequestBody Params params) {
        return podsService.updatePods(params);
    }

    /**
     * Pods 삭제(Delete Pods)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Pods 삭제(Delete Pods)", nickname = "deletePods")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deletePods(Params params) {
        return podsService.deletePods(params);
    }


}
