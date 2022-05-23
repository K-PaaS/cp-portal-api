package org.paasta.container.platform.api.customServices.ingresses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

import java.util.List;
import java.util.Map;

/**
 * Ingresses Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.17
 */
@Data
public class IngressesAdminList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<IngressesListAdminItem> items;
}

@Data
class IngressesListAdminItem{
    private String name;
    private String namespace;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }
}