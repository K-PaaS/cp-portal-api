package org.container.platform.api.storages.persistentVolumes.support;

import lombok.Data;

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
