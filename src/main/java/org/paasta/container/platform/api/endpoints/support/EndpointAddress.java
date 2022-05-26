package org.paasta.container.platform.api.endpoints.support;

import lombok.Data;

/**
 * Endpoints Address Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class EndpointAddress {

    private String hostname;
    private String ip;
    private String nodeName;

}
