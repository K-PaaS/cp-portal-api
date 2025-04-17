package org.container.platform.api.storages.storageClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;
import org.container.platform.api.common.model.CommonStatus;

/**
 * StorageClasses List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Data
public class StorageClassesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<StorageClassesListItem> items;
}

@Data
class StorageClassesListItem {
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
