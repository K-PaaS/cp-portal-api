package org.container.platform.api.storages.storageClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonAnnotations;
import org.container.platform.api.common.model.CommonMetaData;

import java.util.List;

/**
 * StorageClasses Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Data
public class StorageClasses {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;

    private String provisioner;
    private Object parameters;

    @JsonIgnore
    private CommonMetaData metadata;

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

    public Object getParameters() {
        return CommonUtils.procReplaceNullValue(this.parameters);
    }
}
