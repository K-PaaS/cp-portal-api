package org.container.platform.api.clusters.nodes;
import lombok.Data;
import org.container.platform.api.clusters.nodes.support.NodesListItem;
import org.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;


/**
 * Nodes List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NodesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<NodesListItem> items;
}
