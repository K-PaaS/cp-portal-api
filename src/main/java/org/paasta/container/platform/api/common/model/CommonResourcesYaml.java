package org.paasta.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Resources Yaml Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.11
 */
@Data
public class CommonResourcesYaml {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String sourceTypeYaml;

}
