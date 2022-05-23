package org.paasta.container.platform.api.endpoints;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;
import org.paasta.container.platform.api.common.model.CommonSubset;

import java.util.List;

/**
 * Endpoints Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.17
 */
@Data
public class Endpoints {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private CommonMetaData metadata;
    private CommonSpec spec;
    private CommonStatus status;
    private List<CommonSubset> subsets;

}
