package org.paasta.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.paasta.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import java.util.List;

/**
 * LimitRanges Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.22
 */
public class LimitRangesAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String namespace;
    private String creationTimestamp;
    private List<LimitRangesItem> items;

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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public String getName() {
        return metadata.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }


    public List<LimitRangesItem> getItems() {
        return items;
    }

    public void setItems(List<LimitRangesItem> items) {
        this.items = items;
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
