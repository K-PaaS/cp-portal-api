package org.paasta.container.platform.api.customServices.ingresses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;

import java.util.List;
import java.util.Map;

/**
 * Ingresses Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class IngressesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List ingressControllerPort;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<IngressesListItem> items;

}


@Data
class IngressesListItem {
    private String name;
    private String namespace;
    private String creationTimestamp;
    private Map spec;

    @JsonIgnore
    private CommonMetaData metadata;

    public String getName() {
        return name = metadata.getName();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

}
