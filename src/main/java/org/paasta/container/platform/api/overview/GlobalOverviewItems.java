package org.paasta.container.platform.api.overview;

import lombok.Data;
import org.paasta.container.platform.api.overview.support.Count;

import java.util.Map;

/**
 * Global Overview Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.29
 **/
@Data
public class GlobalOverviewItems {

    private String clusterId;
    private String clusterName;
    private String clusterProviderType;
    private String version;
    private Count nodeCount;
    private Count namespaceCount;
    private Count podCount;
    private Count pvCount;
    private Count pvcCount;
    private Map<String, Object> usage;


    public GlobalOverviewItems() {

    }

    public GlobalOverviewItems(String clusterId, String clusterName, String clusterProviderType, String version,
                               Count nodeCount, Count namespaceCount, Count podCount, Count pvCount, Count pvcCount, Map<String, Object> usage) {
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.clusterProviderType = clusterProviderType;
        this.version = version;
        this.nodeCount = nodeCount;
        this.namespaceCount = namespaceCount;
        this.podCount = podCount;
        this.pvCount = pvCount;
        this.pvcCount = pvcCount;
        this.usage = usage;
    }
}