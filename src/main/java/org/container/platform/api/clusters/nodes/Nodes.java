package org.container.platform.api.clusters.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.container.platform.api.clusters.nodes.support.NodesAddress;
import org.container.platform.api.clusters.nodes.support.NodesStatus;
import org.container.platform.api.clusters.nodes.support.NodesSystemInfo;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonAnnotations;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;

/**
 * Nodes Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class Nodes {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;

    private String podsCIDR;
    private List<NodesAddress> addresses;
    private NodesSystemInfo info;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private NodesStatus status;

    public String getName() {
        return name = metadata.getName();
    }

    public String getUid() {
        return uid = metadata.getUid();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

    public String getPodsCIDR() {
        return podsCIDR = spec.getPodCIDR();
    }

    public List<NodesAddress> getAddresses() {
        return addresses = status.getAddresses();
    }

    public NodesSystemInfo getInfo() {
        return info = status.getNodeInfo();
    }
}
