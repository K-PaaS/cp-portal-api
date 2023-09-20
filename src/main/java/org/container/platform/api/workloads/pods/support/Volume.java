package org.container.platform.api.workloads.pods.support;

import lombok.Data;

/**
 * Volume Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
public class Volume {
    private String name;
    private SecretVolumeSource secret;
}