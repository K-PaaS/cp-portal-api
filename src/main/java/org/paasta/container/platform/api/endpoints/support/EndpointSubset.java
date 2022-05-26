package org.paasta.container.platform.api.endpoints.support;

import lombok.Data;

import java.util.List;

/**
 * Endpoint Subset Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class EndpointSubset {

    private List<EndpointAddress> addresses;
    private List<EndpointAddress> notReadyAddresses;
    private List<EndpointPort> ports;
}
