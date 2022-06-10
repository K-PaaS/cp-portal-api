package org.paasta.container.platform.api.clusters.clusters;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * Clusters List Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 **/

@Data
public class ClustersList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<Clusters> items;
}
