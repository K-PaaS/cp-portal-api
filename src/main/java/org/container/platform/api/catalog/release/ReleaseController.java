package org.container.platform.api.catalog.release;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Catalog Release Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2024.03.08
 **/
@Api(value = "ReleaseController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/catalog/releases")
public class ReleaseController {
    private final ReleaseService releaseService;

    /**
     * Instantiates a new Release controller
     *
     * @param releaseService the  Release service
     */
    @Autowired
    ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    /**
     * Release 목록 조회(Get Release list)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 목록 조회(Get Release list)", nickname = "getReleaseList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public CatalogStatus getReleaseList(Params params) {
        return releaseService.getReleaseList(params);
    }


    /**
     * Release 상세 조회(Get Release Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 상세 조회(Get Release Info)", nickname = "getReleaseInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{releases:.+}")
    public CatalogStatus getReleaseInfo(Params params) {
        return releaseService.getReleaseInfo(params);
    }

    /**
     * Release 생성(Install Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 생성(Install Release)", nickname = "installRelease")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping(value = "/{releases:.+}")
    public CatalogStatus installRelease(Params params, @RequestBody Object bodyObject) {
        return releaseService.installRelease(params, bodyObject);
    }

    /**
     * Release 업그레이드(Upgrade Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 업그레이드(Upgrade Release)", nickname = "upgradeRelease")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/{releases:.+}")
    public CatalogStatus upgradeRelease(Params params, @RequestBody Object bodyObject) {
        return releaseService.upgradeRelease(params, bodyObject);
    }


    /**
     * Release 롤백(Rollback Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 롤백(Rollback Release)", nickname = "rollbackRelease")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/{releases:.+}/versions/{reversion:.+}")
    public CatalogStatus rollbackRelease(Params params) {
        return releaseService.rollbackRelease(params);
    }

    /**
     * Release 삭제(Uninstall Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release 삭제(Uninstall Release)", nickname = "uninstallRelease")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{releases:.+}")
    public CatalogStatus uninstallRelease(Params params) {
        return releaseService.uninstallRelease(params);
    }

    /**
     * Release Histories 조회(Get Release Histories)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release Histories 조회(Get Release Histories)", nickname = "getReleaseHistories")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{releases:.+}/histories")
    public CatalogStatus getReleaseHistories(Params params) {
        return releaseService.getReleaseHistories(params);
    }

    /**
     * Release Resources 조회(Get Release Resources)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Release Resources 조회(Get Release Resources)", nickname = "getReleaseResources")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{releases:.+}/resources")
    public CatalogStatus getReleaseResources(Params params) {
        return releaseService.getReleaseResources(params);
    }

}
