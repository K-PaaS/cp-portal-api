package org.container.platform.api.catalog.hub;

import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.container.platform.api.common.Constants.TARGET_CATALOG_API;

@Service
public class ArtifactHubService {
    private final RestTemplateService restTemplateService;

    private final PropertyService propertyService;

    /**
     * Instantiates a new ArtifactHub Service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public ArtifactHubService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
    }

    /**
     * ArtifactHub Repository 목록 조회(Get ArtifactHub Repository List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getRepoListHub(Params params) {
        String listParams = "?offset="+ params.getOffset() + "&limit=" + params.getLimit() +
                "&name=" + params.getRepoName() + "&url=" + params.getRepoUrl();
        return restTemplateService.sendApi(TARGET_CATALOG_API, propertyService.getCpCatalogHubRepoListUrl() + listParams
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * ArtifactHub Packages 목록 조회(Get ArtifactHub Packages List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getPackageListHub(Params params) {
        String listParams = "?offset="+ params.getOffset() + "&limit=" + params.getLimit() +
                "&repo=" + params.getRepoName() + "&query=" + params.getQuery();
        return restTemplateService.sendApi(TARGET_CATALOG_API, propertyService.getCpCatalogHubPackagesListUrl() + listParams
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }

    /**
     * ArtifactHub Helm Packages 정보 조회(Get ArtifactHub Helm Packages Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getHelmPackageInfo(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, propertyService.getCpCatalogHubPackagesGetUrl()
                        .replace("{repositories}", params.getRepositories())
                        .replace("{packages}", params.getPackages())
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * ArtifactHub Helm Packages Values 조회(Get ArtifactHub Helm Packages Values)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getHelmPackageValues(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, propertyService.getCpCatalogHubPackagesValueUrl()
                        .replace("{packageID}", params.getPackageID())
                        .replace("{version}", params.getVersion())
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }

}

