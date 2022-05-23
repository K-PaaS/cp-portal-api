package org.paasta.container.platform.api.endpoints.support;

import lombok.Data;

import java.util.List;

/**
 * EndPoints Details Item Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.02
 */
@Data
public class EndPointsDetailsItemAdmin {

    private String host;
    private String nodes;
    private String ready;
    private List<EndpointPort> ports;
}
