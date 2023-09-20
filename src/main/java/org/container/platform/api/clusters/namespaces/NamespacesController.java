package org.container.platform.api.clusters.namespaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Namespaces Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Api(value = "NamespacesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces")
public class NamespacesController {

    private final NamespacesService namespacesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Namespaces controller
     *
     * @param namespacesService the namespaces service
     */
    @Autowired
    public NamespacesController(NamespacesService namespacesService, ResultStatusService resultStatusService
    ) {
        this.namespacesService = namespacesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Namespaces 목록 조회(Get Namespaces List)
     *
     * @param params the params
     * @return the namespaces list
     */
    @ApiOperation(value = "Namespaces 목록 조회(Get Namespaces List)", nickname = "getNamespacesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public NamespacesList getNamespacesList(Params params) {
    return namespacesService.getNamespacesList(params);
    }



    /**
     * Namespaces 상세 조회(Get Namespaces Detail)
     *
     * @param params the params
     * @return the namespaces detail
     */
    @ApiOperation(value = "Namespaces 상세 조회(Get Namespaces Detail)", nickname = "getNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/{namespace:.+}")
    public Object getNamespaces(Params params) {
        return namespacesService.getNamespaces(params);
    }

    /**
     * Namespaces Yaml 조회(Get Namespaces Yaml)
     *
     * @param params the params
     * @return the namespaces yaml
     */
    @ApiOperation(value = "Namespaces Yaml 조회(Get Namespaces yaml)", nickname = "getNamespacesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{namespace:.+}/yaml")
    public Object getNamespacesYaml(Params params) {
            return namespacesService.getNamespacesYaml(params);
    }

    /**
     * Namespaces 삭제(Delete Namespaces)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Namespaces 삭제(Delete Namespaces)", nickname = "deleteNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{namespace:.+}")
    public ResultStatus deleteNamespaces(Params params) {
        return namespacesService.deleteNamespaces(params);
    }


    /**
     * Namespaces 생성(Create Namespaces)
     *
     * @param params the params
     * @param initTemplate the init template
     * @return the resultStatus
     */
    @ApiOperation(value = "Namespaces 생성(Create Namespaces)", nickname = "initNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body"),
            @ApiImplicitParam(name = "initTemplate", value = "Namespace 생성 정보", required = true, dataType = "NamespacesInitTemplate", paramType = "body")
    })
    @PostMapping
    public ResultStatus initNamespaces(Params params, @RequestBody NamespacesInitTemplate initTemplate) {
        return namespacesService.createInitNamespaces(params, initTemplate);

    }


    /**
     * Namespaces 수정(Update Namespaces)
     *
     * @param params the params
     * @param initTemplate the init template
     * @return the resultStatus
     */
    @ApiOperation(value = "Namespaces 수정(modify Namespaces)", nickname = "modifyInitNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body"),
            @ApiImplicitParam(name = "initTemplate", value = "Namespace 생성 정보", required = true, dataType = "NamespacesInitTemplate", paramType = "body")
    })
    @PutMapping(value = "/{namespace:.+}")
    public ResultStatus modifyInitNamespaces(Params params, @RequestBody NamespacesInitTemplate initTemplate) {
        return namespacesService.modifyInitNamespaces(params, initTemplate);
    }


    /**
     * Namespaces SelectBox 를 위한 Namespaces 목록 조회(Get Namespaces list for SelectBox)
     *
     * @param params the params
     * @return the namespaces list
     */
    @ApiOperation(value = "Namespaces selectbox를 위한 Namespace 목록 조회(Get Namespaces list for SelectBox)", nickname = "getNamespacesListForSelectBox")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/selectbox")
    public Object getNamespacesListForSelectBox(Params params) {
            return namespacesService.getNamespacesListForSelectbox(params);
    }
}