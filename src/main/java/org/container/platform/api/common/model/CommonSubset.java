package org.container.platform.api.common.model;

import lombok.Data;

import java.util.List;

/**
 * Common Subset Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Data
public class CommonSubset {
    private List<CommonAddresses> addresses;
    private List<CommonNotReadyAddresses> notReadyAddresses;
    private List<CommonPort> ports;
}
