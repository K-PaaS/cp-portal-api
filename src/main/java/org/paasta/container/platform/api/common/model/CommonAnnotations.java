package org.paasta.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Annotations Model 클래스
 *
 * @author hoon77
 * @version 1.0
 * @since 2020.12.09
 */

@Data
public class CommonAnnotations {
    private String checkYn;
    private String key;
    private String value;
}
