package org.paasta.container.platform.api.events.support;

import lombok.Data;

/**
 * Events Source Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class EventSource {
    private String component="";
    private String host="";
}
