package org.paasta.container.platform.api.storages.storageClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * StorageClasses List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class StorageClassesListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<StorageClassesListAdminItem> items;
}

@Data
class StorageClassesListAdminItem {
    private String name;
    private String provisioner;
    private Object parameters;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public Object getParameters() {
        return CommonUtils.procReplaceNullValue(this.parameters);
    }
}
