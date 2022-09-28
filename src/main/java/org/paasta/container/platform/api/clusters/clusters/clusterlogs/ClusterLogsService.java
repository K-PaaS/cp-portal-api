package org.paasta.container.platform.api.clusters.clusters.clusterlogs;

import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class ClusterLogsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;

    @Autowired
    public ClusterLogsService(RestTemplateService restTemplateService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
    }

    public ClusterLogsList getClusterLogs(Params params) {
        return (ClusterLogsList) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/logs/" + params.getCluster(), HttpMethod.GET, null, ClusterLogsList.class, params), Constants.RESULT_STATUS_SUCCESS);
    }

}
