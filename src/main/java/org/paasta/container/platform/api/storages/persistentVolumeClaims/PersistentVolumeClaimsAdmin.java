package org.paasta.container.platform.api.storages.persistentVolumeClaims;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsSpec;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsStatus;

import java.util.List;
import java.util.Map;

/**
 * PersistentVolumeClaims Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.18
 */
@Data
public class PersistentVolumeClaimsAdmin {
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

    private String persistentVolumeClaimStatus;
    private String storageClasses;
    private String capacity;
    private String accessMode;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private PersistentVolumeClaimsSpec spec;

    @JsonIgnore
    private PersistentVolumeClaimsStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public String getPersistentVolumeClaimStatus() {
        return status.getPhase();
    }

    public String getStorageClasses() {
        return CommonUtils.procReplaceNullValue(spec.getStorageClassName());
    }

    public Map<String, Object> getCapacity() {
        return CommonUtils.procReplaceNullValue(status.getCapacity());
    }

    public List<String> getAccessMode() {
        return spec.getAccessModes();
    }
}
