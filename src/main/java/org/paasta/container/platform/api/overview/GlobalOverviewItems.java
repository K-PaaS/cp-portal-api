package org.paasta.container.platform.api.overview;

import lombok.Data;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.overview.support.Count;

import java.util.HashMap;
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

    private String resultCode;
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

    public GlobalOverviewItems(String resultCode, String clusterId, String clusterName, String clusterProviderType, String version,
                               Count nodeCount, Count namespaceCount, Count podCount, Count pvCount, Count pvcCount, Map<String, Object> usage) {
        this.resultCode = resultCode;
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



    public GlobalOverviewItems(String resultCode, String clusterId, String clusterName, String clusterProviderType) {
        this.resultCode = resultCode;
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.clusterProviderType = clusterProviderType;
        this.version = Constants.NULL_REPLACE_TEXT;
        this.nodeCount = new Count(0,0);
        this.namespaceCount = new Count(0,0);
        this.podCount = new Count(0,0);
        this.pvCount = new Count(0,0);
        this.pvcCount = new Count(0,0);
        this.usage = new HashMap<>();

    }
}