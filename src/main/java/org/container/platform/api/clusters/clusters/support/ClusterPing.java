package org.container.platform.api.clusters.clusters.support;

import lombok.Data;

@Data
public class ClusterPing {
    private String cluster_id;
    private Integer status_code;
}
