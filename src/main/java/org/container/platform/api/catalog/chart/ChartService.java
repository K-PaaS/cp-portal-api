package org.container.platform.api.catalog.chart;

import org.container.platform.api.catalog.CatalogStatus;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.container.platform.api.common.Constants.TARGET_CATALOG_API;

@Service
public class ChartService {
    private final RestTemplateService restTemplateService;

    private final PropertyService propertyService;

    /**
     * Instantiates a new Chart service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public ChartService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
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


    public String replaceUrl(String url, Params params) {
      String reqUrl = url.replace("{charts}", params.getCharts());
      if(!params.getRepoName().equals(Constants.EMPTY_STRING)) {
          reqUrl += "?repo=" + params.getRepoName();
      }
      return reqUrl;
    }

}

