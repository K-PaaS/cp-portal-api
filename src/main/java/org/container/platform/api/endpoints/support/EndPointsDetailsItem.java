package org.container.platform.api.endpoints.support;

import lombok.Data;
import java.util.List;

/**
 * EndPoints Details Item Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class EndPointsDetailsItem {

    private String host;
    private String nodes;
    private String ready;
    private List<EndpointPort> ports;
}
