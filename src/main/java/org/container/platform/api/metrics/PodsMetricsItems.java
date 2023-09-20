package org.container.platform.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.metrics.custom.ContainerMetrics;
import org.container.platform.api.metrics.custom.Quantity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Data
public class PodsMetricsItems {

    private String name;
    private String namespace;
    private String creationTimestamp;
    private String timestamp;
    private String window;
    private List<ContainerMetrics> containers;

    @JsonIgnore
    private CommonMetaData metadata;

    private String clusterId;
    private String clusterName;


    public String getName() {
        return CommonUtils.procReplaceNullValue(metadata.getName());
    }

    public String getNamespace() {return CommonUtils.procReplaceNullValue(metadata.getNamespace());
}
    public String getCreationTimestamp() {
        return CommonUtils.procReplaceNullValue(metadata.getCreationTimestamp());
    }

    public Map<String, String> getLabels() {
        return metadata.getLabels();
    }

    public String getTimestamp() {
        return CommonUtils.procReplaceNullValue(timestamp);
    }

    public String getWindow() {
        return CommonUtils.procReplaceNullValue(window);
    }
}
