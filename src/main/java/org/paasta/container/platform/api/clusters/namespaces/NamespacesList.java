package org.paasta.container.platform.api.clusters.namespaces;

import lombok.Data;
import org.paasta.container.platform.api.clusters.namespaces.support.NamespacesListItem;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;


/**
 * Namespaces List Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NamespacesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<NamespacesListItem> items;
}

