package org.paasta.container.platform.api.clusters.namespaces;

import lombok.Data;

/**
 * Namespaces Yaml Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NamespacesYaml {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String sourceTypeYaml;
}
