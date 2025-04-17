package org.container.platform.api.customServices.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.container.platform.api.common.model.*;

/**
 * CustomServices List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class CustomServicesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<CustomServicesListItem> items;
}

@Data
class CustomServicesListItem {
    private String name;
    private String namespace;
    private String type;
    private String clusterIP;
    private List<CommonPort> ports;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getType() {
        return spec.getType();
    }

    public String getClusterIP() {
        return spec.getClusterIP();
    }

    public List<CommonPort> getPorts() { return spec.getPorts(); }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
