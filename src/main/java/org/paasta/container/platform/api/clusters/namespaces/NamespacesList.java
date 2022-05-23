package org.paasta.container.platform.api.clusters.namespaces;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * Namespaces List Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Data
public class NamespacesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<Namespaces> items;
}
