package org.container.platform.api.workloads.pods.support;

import lombok.Data;

/**
 * Secret Volume Source Model 클래스
 * (Secret 클래스가 아님)
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.07
 */
@Data
class SecretVolumeSource {
    private String secretName;
    private String defaultMode;
}
