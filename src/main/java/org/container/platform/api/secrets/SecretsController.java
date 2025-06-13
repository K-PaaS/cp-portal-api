package org.container.platform.api.secrets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Secrets Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/

@Tag(name = "SecretsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/secrets")
public class SecretsController {

    private final SecretsService secretsService;

    /**
     * Instantiates a new Secrets service
     *
     * @param secretsService the Secrets service
     */
    @Autowired
    public SecretsController(SecretsService secretsService){
        this.secretsService = secretsService;
    }

    /**
     * Secrets 리스트 조회(Get Secrets List)
     *
     * @param params the params
     * @return the Secrets list
     */
    @Operation(summary = "Secrets 목록 조회(Get Secrets List)", operationId = "getSecretsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public SecretsList getSecretsList(Params params) {

        return secretsService.getSecretsList(params);
    }

    /**
     * Secrets 상세 조회(Get Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    @Operation(summary = "Secrets 상세 조회(Get Secrets Detail)", operationId = "getSecrets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Object getSecrets(Params params) {
        return secretsService.getSecrets(params);
    }

    /**
     * Secrets YAML 조회(Get Secrets YAML)
     *
     * @param params the params
     * @return the Secrets yaml
     */
    @Operation(summary = "Secrets YAML 조회(Get Secrets yaml)", operationId = "getSecretsYaml")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}/yaml")
    public CommonResourcesYaml getSecretsYaml(Params params) {
        return secretsService.getSecretsYaml(params);
    }

    /**
     * Secrets 생성(Create Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Secrets 생성(Create Secrets)", operationId = "createSecrets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createSecrets(@RequestBody Params params) throws Exception {
        if (params.getYaml().contains("---")) {
            Object object = ResourceExecuteManager.commonControllerExecute(params);
            return object;
        }
        return secretsService.createSecrets(params);
    }

    /**
     * Secrets 삭제(Delete Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Secrets 삭제(Delete Secrets)", operationId = "deleteSecrets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteSecrets(Params params) {
        return secretsService.deleteSecrets(params);
    }

    /**
     * Secrets 수정(Update Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Secrets 수정(Update Secrets)", operationId = "updateSecrets")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateSecrets(@RequestBody Params params) {
        return secretsService.updateSecrets(params);
    }

}
