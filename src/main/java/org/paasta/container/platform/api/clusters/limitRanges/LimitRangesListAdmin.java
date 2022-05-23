package org.paasta.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;

import java.util.List;
import java.util.Map;

/**
 * LimitRanges List Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.22
 */
@Data
public class LimitRangesListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<LimitRangesListAdminItem> items;

}

class LimitRangesListAdminItem {

    private String name;
    private String namespace;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() { return metadata.getName(); }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public CommonMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(CommonMetaData metadata) {
        this.metadata = metadata;
    }

    public CommonSpec getSpec() {
        return spec;
    }

    public void setSpec(CommonSpec spec) {
        this.spec = spec;
    }
}