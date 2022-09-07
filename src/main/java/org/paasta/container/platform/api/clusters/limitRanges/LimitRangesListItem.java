package org.paasta.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;

/**
 * LimitRanges List Items 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */

@Data
public class LimitRangesListItem {

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