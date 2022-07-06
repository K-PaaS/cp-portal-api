package org.paasta.container.platform.api.storages.persistentVolumeClaims.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;

import java.util.Map;

@Data
public class PersistentVolumeClaimsListItem {

    private String name;
    private String namespace;
    private String persistentVolumeClaimStatus;
    private String volume;
    private String capacity;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private PersistentVolumeClaimsSpec spec;

    @JsonIgnore
    private PersistentVolumeClaimsStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public String getPersistentVolumeClaimStatus() {
        return status.getPhase();
    }

    public String getVolume() {
        return CommonUtils.procReplaceNullValue(spec.getVolumeName());
    }

    public Map<String, Object> getCapacity() {
        return CommonUtils.procReplaceNullValue(status.getCapacity());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
