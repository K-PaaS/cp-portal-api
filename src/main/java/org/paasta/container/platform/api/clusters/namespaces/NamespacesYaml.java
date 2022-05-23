package org.paasta.container.platform.api.clusters.namespaces;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

import java.util.Map;

/**
 * Namespaces Yaml Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.02
 */
@Data
public class NamespacesYaml {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String sourceTypeYaml;
}
