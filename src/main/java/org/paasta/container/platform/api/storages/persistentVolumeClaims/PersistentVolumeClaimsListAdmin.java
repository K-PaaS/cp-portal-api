package org.paasta.container.platform.api.storages.persistentVolumeClaims;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsSpec;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsStatus;

/**
 * PersistentVolumeClaims List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.18
 */
@Data
public class PersistentVolumeClaimsListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PersistentVolumeClaimsListAdminItem> items;

}

@Data
class PersistentVolumeClaimsListAdminItem {

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
