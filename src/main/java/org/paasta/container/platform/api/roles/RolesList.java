package org.paasta.container.platform.api.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;

/**
 * Roles List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class RolesList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<RolesListItem> items;

}

@Data
class RolesListItem {
    private String name;
    private String namespace;
    private String creationTimestamp;

    // item with user
    private String checkYn = Constants.CHECK_N;
    private String userType = Constants.NOT_ASSIGNED_ROLE;
    private String isNamespaceAdminRole = Constants.CHECK_N;


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
