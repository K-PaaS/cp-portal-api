package org.container.platform.api.secrets;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.ResourceExecuteManager;
import org.container.platform.api.secrets.vaultSecrets.DatabaseConnections;
import org.container.platform.api.secrets.vaultSecrets.DatabaseConnectionsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Secrets Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/

@Api(value = "SecretsController v1")
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
    @ApiOperation(value = "Secrets 목록 조회(Get Secrets List)", nickname = "getSecretsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping
    public SecretsList getSecretsList(Params params) {

        return secretsService.getSecretsList(params);
    }

    /**
     * Vault Secrets 리스트 조회(Get Vault Secrets List)
     *
     * @param params the params
     * @return the Vault Secrets list
     */
    @ApiOperation(value = "Vault Secrets 목록 조회(Get Vault Secrets List)", nickname = "getVaultSecretsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/vault")
    public DatabaseConnectionsList getVaultSecretsList(Params params) {

        return secretsService.getVaultSecretsList(params);
    }


    /**
     * Secrets 상세 조회(Get Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    @ApiOperation(value = "Secrets 상세 조회(Get Secrets Detail)", nickname = "getSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getSecrets(Params params) {
        return secretsService.getSecrets(params);
    }

    /**
     * Secrets 상세 조회(Get Vault Secrets Detail)
     *
     * @param params the params
     * @return the Vault Secrets detail
     */
    /*@ApiOperation(value = "Vault Secrets 상세 조회(Get Vault Secrets Detail)", nickname = "getVaultSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/vault/{resourceName:.+}")
    public Object getVaultSecrets(Params params) {
        return secretsService.getVaultSecrets(params);
    }*/


    /**
     * Secrets YAML 조회(Get Secrets YAML)
     *
     * @param params the params
     * @return the Secrets yaml
     */
    @ApiOperation(value = "Secrets YAML 조회(Get Secrets yaml)", nickname = "getSecretsYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Secrets 생성(Create Secrets)", nickname = "createSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
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
    @ApiOperation(value = "Secrets 삭제(Delete Secrets)", nickname = "deleteSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteSecrets(Params params) {
        return secretsService.deleteSecrets(params);
    }

    /**
     * Secrets 삭제(Delete Vault Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    /*@ApiOperation(value = "Secrets 삭제(Delete Vault Secrets)", nickname = "deleteVaultSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @DeleteMapping("/{resourceName:.+}")
    public ResultStatus deleteVaultSecrets(Params params) {
        return secretsService.deleteVaultSecrets(params);
    }*/


    /**
     * Secrets 수정(Update Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Secrets 수정(Update Secrets)", nickname = "updateSecrets")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @PutMapping("/{resourceName:.+}")
    public ResultStatus updateSecrets(@RequestBody Params params) {
        return secretsService.updateSecrets(params);
    }
}
