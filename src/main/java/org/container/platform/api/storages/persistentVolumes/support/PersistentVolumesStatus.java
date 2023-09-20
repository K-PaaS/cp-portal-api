package org.container.platform.api.storages.persistentVolumes.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonCondition;

import java.util.List;
import java.util.Map;

/**
 * PersistentVolumes Status Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.19
 */
@Data
public class PersistentVolumesStatus {
    private String phase;
}
