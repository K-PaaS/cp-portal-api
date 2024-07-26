package org.container.platform.api.catalog.hub;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catalog ArtifactHub Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2024.04.08
 **/
@Api(value = "ArtifactHubController v1")
@RestController
@RequestMapping("/catalog/hub")
public class ArtifactHubController {
    private final ArtifactHubService artifactHubService;

    /**
     * Instantiates a new ArtifactHub controller
     *
     * @param artifactHubService the ArtifactHub service
     */
    @Autowired
    ArtifactHubController(ArtifactHubService artifactHubService) {
        this.artifactHubService = artifactHubService;
    }

    /**
     * ArtifactHub Repository 목록 조회(Get ArtifactHub Repository List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "ArtifactHub Repository 목록 조회(Get ArtifactHub Repository List)", nickname = "getRepoListHub")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "query", dataTypeClass = Params.class)
    })
    @GetMapping("/repositories")
    public CatalogStatus getRepoListHub(Params params) {
        return artifactHubService.getRepoListHub(params);
    }


    /**
     * ArtifactHub Packages 목록 조회(Get ArtifactHub Packages List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "ArtifactHub Packages 목록 조회(Get ArtifactHub Packages List)", nickname = "getPackageListHub")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "query", dataTypeClass = Params.class)
    })
    @GetMapping("/packages")
    public CatalogStatus getPackageListHub(Params params) {
        return artifactHubService.getPackageListHub(params);
    }


    /**
     * ArtifactHub Helm Packages 정보 조회(Get ArtifactHub Helm Packages Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "ArtifactHub Helm Packages 정보 조회(Get ArtifactHub Helm Packages Info)", nickname = "getHelmPackageInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "query", dataTypeClass = Params.class)
    })
    @GetMapping("/packages/{repositories:.+}/{packages:.+}")
    public CatalogStatus getHelmPackageInfo(Params params) {
        return artifactHubService.getHelmPackageInfo(params);
    }

    /**
     * ArtifactHub Helm Packages Values 조회(Get ArtifactHub Helm Packages Values)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    @ApiOperation(value = "ArtifactHub Helm Packages Values 조회(Get ArtifactHub Helm Packages Values)", nickname = "getHelmPackageValues")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "query", dataTypeClass = Params.class)
    })
    @GetMapping("/packages/{packageID:.+}/{version:.+}/values")
    public CatalogStatus getHelmPackageValues(Params params) {
        return artifactHubService.getHelmPackageValues(params);
    }

}
