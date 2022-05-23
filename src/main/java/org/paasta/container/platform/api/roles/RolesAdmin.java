package org.paasta.container.platform.api.roles;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.roles.supports.RolesRule;


/**
 * Roles Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class RolesAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private String namespace;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;
    private List<RolesRule> rules;

    @JsonIgnore
    private CommonMetaData metadata;

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
