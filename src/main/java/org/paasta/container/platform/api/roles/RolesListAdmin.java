package org.paasta.container.platform.api.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;

/**
 * Roles List Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class RolesListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<RolesListAdminItem> items;

}

@Data
class RolesListAdminItem {
    private String name;
    private String namespace;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
