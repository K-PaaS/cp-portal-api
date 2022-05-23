package org.paasta.container.platform.api.endpoints.support;

import lombok.Data;

import java.util.List;

/**
 * Endpoint Subset Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.02
 */
@Data
public class EndpointSubset {

    private List<EndpointAddress> addresses;
    private List<EndpointAddress> notReadyAddresses;
    private List<EndpointPort> ports;
}
