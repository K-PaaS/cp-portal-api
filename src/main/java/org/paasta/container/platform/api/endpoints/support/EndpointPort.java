package org.paasta.container.platform.api.endpoints.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Endpoints Port Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.02
 */
@Data
public class EndpointPort {

    @JsonIgnore
    private String appProtocol;
    private String name;
    private Integer port;
    private String protocol;
}
