package org.container.platform.api.storages.persistentVolumes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.storages.persistentVolumes.support.ObjectReference;
import org.container.platform.api.storages.persistentVolumes.support.PersistentVolumesListItem;
import org.container.platform.api.storages.persistentVolumes.support.PersistentVolumesSpec;
import org.container.platform.api.storages.persistentVolumes.support.PersistentVolumesStatus;

/**
 * PersistentVolumes List Admin Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Data
public class PersistentVolumesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PersistentVolumesListItem> items;
}