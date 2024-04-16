package org.container.platform.api.catalog.release;

import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.container.platform.api.common.Constants.TARGET_CATALOG_API;


@Service
public class ReleaseService {
    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Release service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public ReleaseService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
    }

    /**
     * Release 목록 조회(Get Release list)
     *
     * @param params the params
     * @return the list
     */
    public CatalogStatus getReleaseList(Params params) {
        String listParams = "?offset="+ params.getOffset() + "&limit=" + params.getLimit() + "&searchName=" + params.getSearchName();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseListUrl(), params) + listParams
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * Release 상세 조회(Get Release Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getReleaseInfo(Params params) {
        String query = "?userDefined=" + params.getUserDefined();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseGetUrl() + query, params)
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * Release 생성(Install Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus installRelease(Params params, Object bodyObject) {
        String query = "?preview="+ params.getPreview() + "&userDefined=" + params.getUserDefined();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseGetUrl() + query, params)
                ,HttpMethod.POST, bodyObject, CatalogStatus.class, params);
    }

    /**
     * Release 업그레이드(Upgrade Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus upgradeRelease(Params params, Object bodyObject) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseGetUrl(), params)
                ,HttpMethod.PUT, bodyObject, CatalogStatus.class, params);
    }

    /**
     * Release 롤백(Rollback Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus rollbackRelease(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseRollbackUrl(), params)
                        .replace("{reversion}", params.getReversion())
                ,HttpMethod.PUT, null, CatalogStatus.class, params);
    }


    /**
     * Release 삭제(Uninstall Release)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus uninstallRelease(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseGetUrl(), params)
                ,HttpMethod.DELETE, null, CatalogStatus.class, params);
    }

    /**
     * Release Histories 조회(Get Release Histories)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getReleaseHistories(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseHistoriesUrl(), params)
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }

    /**
     * Release Resources 조회(Get Release Resources)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getReleaseResources(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogReleaseResourcesUrl(), params)
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    public String replaceUrl(String url, Params params) {
        return url.replace("{clusterId}", params.getCluster()).replace("{namespace}",params.getNamespace())
                .replace("{release}",params.getReleases());
    }
}

