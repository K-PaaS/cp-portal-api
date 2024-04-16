package org.container.platform.api.catalog.repo;

import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.container.platform.api.common.Constants.TARGET_CATALOG_API;

@Service
public class RepositoryService {
    private final RestTemplateService restTemplateService;

    private final PropertyService propertyService;

    /**
     * Instantiates a new Repository service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public RepositoryService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
    }

    /**
     * Repository 목록 조회(Get Repository list)
     *
     * @param params the params
     * @return the list
     */
    public CatalogStatus getRepoList(Params params) {
        String listParams = "?offset="+ params.getOffset() + "&limit=" + params.getLimit() + "&searchName=" + params.getSearchName();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogRepoListUrl(), params) + listParams
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * Repository 추가(Add Repository)
     *
     * @param params the params
     * @param bodyObject the object
     * @return the CatalogStatus
     */
    public CatalogStatus addRepo(Params params, Object bodyObject) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogRepoGetUrl(), params),
                HttpMethod.POST, bodyObject, CatalogStatus.class, params);
    }

    /**
     * Repository 제거(Remove Repository)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus removeRepo(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogRepoGetUrl(), params),
                HttpMethod.DELETE, null, CatalogStatus.class, params);
    }

    /**
     * Repository 업데이트(Update Repository)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus updateRepo(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogRepoGetUrl(), params),
                HttpMethod.PUT, null, CatalogStatus.class, params);
    }


    /**
     * Repository 차트 목록 조회(Get Repository Chart List)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getRepoChartsList(Params params) {
        String listParams = "?offset="+ params.getOffset() + "&limit=" + params.getLimit() + "&searchName=" + params.getSearchName();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogRepoChartsUrl(), params) + listParams,
                HttpMethod.GET, null, CatalogStatus.class, params);
    }

    /**
     * 차트 버전 목록 조회(Get Chart Versions)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getChartVersions(Params params) {
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogChartVersionsUrl(), params)
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    /**
     * 차트 정보 조회(Get Chart Info)
     *
     * @param params the params
     * @return the CatalogStatus
     */
    public CatalogStatus getChartInfo(Params params) {
        String infoParams = "?info="+ params.getInfo() + "&version=" + params.getVersion();
        return restTemplateService.sendApi(TARGET_CATALOG_API, replaceUrl(propertyService.getCpCatalogChartInfoUrl(), params) + infoParams
                ,HttpMethod.GET, null, CatalogStatus.class, params);
    }


    public String replaceUrl(String url, Params params) {
      return url.replace("{repositories}", params.getRepositories()).replace("{charts}", params.getCharts());
    }

}

