package org.container.platform.api.workloads.pods;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.container.platform.api.workloads.pods.support.PodsLabels;
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
@Tag(name = "PodsController v1")
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
    @Operation(summary = "Pods 목록 조회(Get Pods List)", operationId = "getPodsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Selector 값에 따른 Pods 목록 조회(Get Pods By Selector)", operationId = "getPodListBySelector")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Node 명에 따른 Pods 목록 조회(Get Pods By Node)", operationId = "getPodListByNode")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Pods 상세 조회(Get Pods Detail)", operationId = "getPods")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Pods YAML 조회(Get Pods Yaml)", operationId = "getPodsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Pods 생성(Create Pods)", operationId = "createPods")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Pods 수정(Update Pods)", operationId = "updatePods")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Pods 삭제(Delete Pods)", operationId = "deletePods")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deletePods(Params params) {
        return podsService.deletePods(params);
    }

    /**
     * Namespaces의 Labels 목록 조회(Get Labels List By Namespaces)
     *
     * @param params the params
     * @return the pods list
     */
    @Operation(summary = "Namespaces의 Labels 목록 조회(Get Labels List By Namespaces)", operationId = "getLabelsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping("/labels")
    @ResponseBody
    public PodsLabels getLabelsList(Params params) {
        return podsService.getLabelsList(params);
    }
}
