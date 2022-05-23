package org.paasta.container.platform.api.storages.storageClasses;

import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsSpec;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsStatus;

/**
 * StorageClasses Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class StorageClasses {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private CommonMetaData metadata;
    private PersistentVolumeClaimsSpec spec;
    private PersistentVolumeClaimsStatus status;

    private Map<String, Object> source;
    private String sourceTypeYaml;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
