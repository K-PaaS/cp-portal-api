package org.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Not Ready Addresses Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
class CommonNotReadyAddresses {
    private String ip;
    private String nodeName;
}
