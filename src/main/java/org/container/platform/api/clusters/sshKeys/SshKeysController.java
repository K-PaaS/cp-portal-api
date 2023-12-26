package org.container.platform.api.clusters.sshKeys;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

@Api(value = "sshKeysController v1")
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
    @ApiOperation(value = "SshKeys 목록 조회(Get SshKeys List)", nickname = "getSshKeysList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "SshKeys 상세 조회(Get SshKeys)", nickname = "getSshKeys")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "SshKeys 삭제(Delete SshKeys)", nickname = "deleteSshKeys")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "SshKeys 생성(Create SshKeys)", nickname = "initSshKeys")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "SshKeys 수정(Update SshKeys)", nickname = "updateSshKeys")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "SshKeys 타입 별 목록 조회(Get SshKeys List By Provider)", nickname = "getSshKeysListByProvider")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/provider/{providerType:.+}")
    public SshKeysList getSshKeysListByProvider(Params params) {
        return sshKeysService.getSshKeysListByProvider(params);
    }
}