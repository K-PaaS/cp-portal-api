package org.container.platform.api.storages.persistentVolumeClaims.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonLabelSelector;
import org.container.platform.api.common.model.CommonResourceRequirement;
import org.container.platform.api.common.model.CommonTypedLocalObjectReference;

import java.util.List;

/**
 * PersistentVolumeClaims Spec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.18
 */
@Data
public class PersistentVolumeClaimsSpec {
    private List<String> accessModes;
    private String volumeName;
    private String storageClassName;
    private String volumeMode;
    private CommonTypedLocalObjectReference dataSource;
    private CommonResourceRequirement resources;
    private CommonLabelSelector selector;
}
