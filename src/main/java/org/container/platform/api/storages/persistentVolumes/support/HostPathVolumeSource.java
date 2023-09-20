package org.container.platform.api.storages.persistentVolumes.support;

import lombok.Data;

/**
 * Host Path Volume Source 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.28
 */
@Data
public class HostPathVolumeSource {
    private String path;
    private String type;
}
