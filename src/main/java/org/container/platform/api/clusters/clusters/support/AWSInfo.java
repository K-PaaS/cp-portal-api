package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

/**
 * AWSInfo Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class AWSInfo {
    private String accessKey = Constants.EMPTY_STRING;
    private String secretKey = Constants.EMPTY_STRING;
}
