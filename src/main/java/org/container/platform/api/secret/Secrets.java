package org.container.platform.api.secret;

import lombok.Data;
import org.container.platform.api.common.model.CommonMetaData;

/**
 * Secrets Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.16
 **/
@Data
public class Secrets {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private CommonMetaData metadata;
    private String type;
}
