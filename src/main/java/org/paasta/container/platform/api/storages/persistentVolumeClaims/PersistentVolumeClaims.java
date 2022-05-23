package org.paasta.container.platform.api.storages.persistentVolumeClaims;

import java.util.Map;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsSpec;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsStatus;

/**
 * PersistentVolumeClaims Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.18
 */
@Data
public class PersistentVolumeClaims {
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
