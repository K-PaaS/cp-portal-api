package org.paasta.container.platform.api.customServices.services;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * CustomServices List Admin Model 클래스
 *
 * @author kjh
 * @version 1.0
 * @since 2020.09.10
 */
@Data
public class CustomServicesListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<CustomServicesListAdminItem> items;
}

@Data
class CustomServicesListAdminItem {
    private String name;
    private String namespace;
    private String type;
    private String clusterIP;
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

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
