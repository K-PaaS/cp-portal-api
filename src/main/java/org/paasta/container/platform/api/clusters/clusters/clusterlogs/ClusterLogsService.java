package org.paasta.container.platform.api.clusters.clusters.clusterlogs;

import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class ClusterLogsService {
    private final RestTemplateService restTemplateService;

    @Autowired
    public ClusterLogsService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    public ClusterLogsList getClusterLogs(Params params) {
        return restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/clusters/logs/" + params.getCluster(), HttpMethod.GET, null, ClusterLogsList.class, params);
    }

}
