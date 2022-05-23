package org.paasta.container.platform.api.storages.persistentVolumes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.storages.persistentVolumes.support.ObjectReference;
import org.paasta.container.platform.api.storages.persistentVolumes.support.PersistentVolumesSpec;
import org.paasta.container.platform.api.storages.persistentVolumes.support.PersistentVolumesStatus;

/**
 * PersistentVolumes List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.19
 */
@Data
public class PersistentVolumesListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PersistentVolumesListAdminItem> items;
}

@Data
class PersistentVolumesListAdminItem {
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
