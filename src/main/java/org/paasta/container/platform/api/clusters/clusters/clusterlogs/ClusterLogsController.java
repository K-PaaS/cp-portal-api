package org.paasta.container.platform.api.clusters.clusters.clusterlogs;

import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clusters/logs")
public class ClusterLogsController {
    private final ClusterLogsService clusterLogsService;

    @Autowired
    public ClusterLogsController(ClusterLogsService clusterLogsService) {
        this.clusterLogsService = clusterLogsService;
    }

    @GetMapping(value = "/{cluster:.+}")
    public ClusterLogsList getClusterLogs(Params params) {
        return clusterLogsService.getClusterLogs(params);
    }
}
