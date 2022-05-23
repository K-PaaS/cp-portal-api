package org.paasta.container.platform.api.endpoints;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * Endpoints List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.17
 */
@Data
public class EndpointsListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<Endpoints> items;
}
