package org.container.platform.api.clusters.namespaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "NamespacesController v1")
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
    @Operation(summary = "Namespaces 목록 조회(Get Namespaces List)", operationId = "getNamespacesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Namespaces 상세 조회(Get Namespaces Detail)", operationId = "getNamespaces")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Namespaces Yaml 조회(Get Namespaces yaml)", operationId = "getNamespacesYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Namespaces 삭제(Delete Namespaces)", operationId = "deleteNamespaces")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Namespaces 생성(Create Namespaces)", operationId = "initNamespaces")
    @Parameters({
            @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class)),
            @Parameter(name = "initTemplate", description = "Namespace 리소스 정보", required = true, schema=@Schema(implementation = NamespacesInitTemplate.class)),
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
    @Operation(summary = "Namespaces 수정(modify Namespaces)", operationId = "modifyInitNamespaces")
    @Parameters({
            @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class)),
            @Parameter(name = "initTemplate", description = "Namespace 리소스 정보", required = true, schema=@Schema(implementation = NamespacesInitTemplate.class)),
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
    @Operation(summary = "Namespaces selectbox를 위한 Namespace 목록 조회(Get Namespaces list for SelectBox)", operationId = "getNamespacesListForSelectBox")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping("/selectbox")
    public Object getNamespacesListForSelectBox(Params params) {
            return namespacesService.getNamespacesListForSelectbox(params);
    }


    /**
     * Traffic 정책 여부 확인 (check Traffic Policy Managed)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Traffic 정책 여부 확인 (check Traffic Policy Managed)", operationId = "checkTrafficPolicyManaged")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping("/checkTrafficPolicyManaged")
    public ResultStatus checkTrafficPolicyManaged(Params params) {
        return namespacesService.checkTrafficPolicyManaged(params);
    }
}