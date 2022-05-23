package org.paasta.container.platform.api.endpoints.support;

import lombok.Data;

/**
 * Endpoints Address Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.02
 */
@Data
public class EndpointAddress {

    private String hostname;
    private String ip;
    private String nodeName;

}
