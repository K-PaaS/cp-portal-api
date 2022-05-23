package org.paasta.container.platform.api.workloads.replicaSets;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * ReplicaSets List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.10
 */
@Data
public class ReplicaSetsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<ReplicaSets> items;

}
