package org.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;

import java.util.List;
import java.util.Map;

/**
 * LimitRanges Template List Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 **/
@Data
public class LimitRangesTemplateList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;

    private List<LimitRangesTemplateItem> items;
}

@Data
class LimitRangesTemplateItem {
    private String name;
    private String type;
    private String resource;
    private String min;
    private String max;
    private String defaultRequest;
    private String defaultLimit;

    private String checkYn;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCheckYn() {
        return checkYn;
    }

    public void setCheckYn(String checkYn) {
        this.checkYn = checkYn;
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