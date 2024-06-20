package org.container.platform.api.catalog.repo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Catalog Repository Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2024.03.08
 **/
@Api(value = "RepositoryController v1")
@RestController
@RequestMapping("/catalog/repositories")
public class RepositoryController {
    private final RepositoryService repositoryService;


    /**
     * Instantiates a new Repository controller
     *
     * @param repositoryService the Repository service
     */
    @Autowired
    RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Repository 목록 조회(Get Repository list)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Repository 목록 조회(Get Repository list)", nickname = "getRepoList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public CatalogStatus getRepoList(Params params) {
        return repositoryService.getRepoList(params);
    }


    /**
     * Repository 추가(Add Repository)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Repository 추가(Add Repository)", nickname = "addRepo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public CatalogStatus addRepo(Params params, @RequestBody Object bodyObject) {
        return repositoryService.addRepo(params, bodyObject);
    }

    /**
     * Repository 제거(Remove Repository)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Repository 제거(Remove Repository)", nickname = "removeRepo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{repositories:.+}")
    public CatalogStatus removeRepo(Params params) {
        return repositoryService.removeRepo(params);
    }

    /**
     * Repository 업데이트(Update Repository)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Repository 업데이트(Update Repository)", nickname = "updateRepo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/{repositories:.+}")
    public CatalogStatus updateRepo(Params params) {
        return repositoryService.updateRepo(params);
    }


    /**
     * Repository 차트 목록 조회(Get Repository Chart List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "Repository 차트 목록(Get Repository Chart List)", nickname = "getRepoChartsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{repositories:.+}/charts")
    public CatalogStatus getRepoChartsList(Params params) {
        return repositoryService.getRepoChartsList(params);
    }


    /**
     * 차트 버전 목록 조회(Get Chart Versions)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "차트 버전 목록 조회(Get Chart Versions)", nickname = "getChartVersions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{repositories:.+}/charts/{charts:.+}/versions")
    public CatalogStatus getChartVersions(Params params) {
        return repositoryService.getChartVersions(params);}

    /**
     * 차트 정보 조회(Get Chart Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "차트 정보 조회(Get Chart Info)", nickname = "getChartInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{repositories:.+}/charts/{charts:.+}/info")
    public CatalogStatus getChartInfo(Params params) {
        return repositoryService.getChartInfo(params);
    }


    /**
     * 차트 캐시 삭제 (Clear Repo Cache)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "차트 캐시 삭제 (Clear Repo Cache)", nickname = "clearChartCache")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PreAuthorize("@webSecurity.checkisGlobalAdmin()")
    @DeleteMapping(value = "/cache/clear")
    public CatalogStatus clearChartCache(Params params) {
        return repositoryService.clearChartCache(params);
    }
}
