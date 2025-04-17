package org.container.platform.api.clusters.sshKeys;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.ResultStatusService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * SshKeys Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 */

@Tag(name = "SshKeysController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/keys")
public class SshKeysController {

    private final SshKeysService sshKeysService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SshKeys controller
     *
     * @param sshKeysService the sshKeys service
     */
    @Autowired
    public SshKeysController(SshKeysService sshKeysService, ResultStatusService resultStatusService
    ) {
        this.sshKeysService = sshKeysService;
        this.resultStatusService = resultStatusService;
    }

    /**
     * SshKeys 목록 조회(Get SshKeys list)
     *
     * @param params the params
     * @return the sshKeys list
     */
    @Operation(summary = "SshKeys 목록 조회(Get SshKeys List)", operationId = "getSshKeysList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public SshKeysList getSshKeysList(Params params) {
        return sshKeysService.getSshKeysList(params);
    }

    /**
     * SshKeys 상세 조회(Get sshKeys detail)
     *
     * @param params the params
     * @return the sshKeys detail
     */
    @Operation(summary = "SshKeys 상세 조회(Get SshKeys)", operationId = "getSshKeys")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceUid:.+}")
    public Object getSshKeys(Params params) {

        return sshKeysService.getSshKeys(params);
    }

    /**
     * SshKeys 삭제(Delete SshKeys)
     *
     * @param params the params
     * @return the Object
     */
    @Operation(summary = "SshKeys 삭제(Delete SshKeys)", operationId = "deleteSshKeys")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @DeleteMapping(value = "/{resourceUid:.+}")
    public Object deleteSshKeys(Params params) {
        return sshKeysService.deleteSshKeys(params);
    }


    /**
     * SshKeys 생성(Create SshKeys)
     *
     * @param params the params
     * @return Object
     */
    @Operation(summary = "SshKeys 생성(Create SshKeys)", operationId = "initSshKeys")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object initSshKeys(@RequestBody Params params) {
        return sshKeysService.createSshKeys(params);
    }

    /**
     * SshKeys 수정(Update SshKeys)
     *
     * @param params the params
     * @return Object
     */
    @Operation(summary = "SshKeys 수정(Update SshKeys)", operationId = "updateSshKeys")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @PutMapping
    public Object modifyInitSshKeys(@RequestBody Params params) {
        return sshKeysService.modifyInitSshKeys(params);
    }


    /**
     * SshKeys 타입 별 목록 조회(Get SshKeys list By Provider)
     *
     * @param params the params
     * @return the sshKeys list
     */
    @Operation(summary = "SshKeys 타입 별 목록 조회(Get SshKeys List By Provider)", operationId = "getSshKeysListByProvider")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/provider/{providerType:.+}")
    public SshKeysList getSshKeysListByProvider(Params params) {
        return sshKeysService.getSshKeysListByProvider(params);
    }
}