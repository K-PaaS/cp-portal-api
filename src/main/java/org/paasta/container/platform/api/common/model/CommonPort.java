package org.paasta.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Port Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Data
public class CommonPort {
    private String name;
    private String port;
    private String protocol;
}
