package org.paasta.container.platform.api.storages.persistentVolumes.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;

import java.util.List;

@Data
public class PersistentVolumesListItem {
    private String name;
    private Object capacity;
    private String accessMode;
    private String persistentVolumeStatus;
    private ObjectReference claim;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private PersistentVolumesSpec spec;

    @JsonIgnore
    private PersistentVolumesStatus status;

    public String getName() {
        return metadata.getName();
    }

    public Object getCapacity() {
        return CommonUtils.procReplaceNullValue(spec.getCapacity());
    }

    public List<String> getAccessMode() {
        return spec.getAccessModes();
    }

    public String getPersistentVolumeStatus() {
        return status.getPhase();
    }

    public ObjectReference getClaim() {
        return CommonUtils.procReplaceNullValue(spec.getClaimRef());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}

