package org.paasta.container.platform.api.clusters.cloudAccounts;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * CloudAccountsList Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class CloudAccountsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<CloudAccounts> items;
}
