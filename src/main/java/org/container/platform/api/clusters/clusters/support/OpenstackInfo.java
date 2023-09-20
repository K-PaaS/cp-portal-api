package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

/**
 * OpenstackInfo Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class OpenstackInfo {
    private String auth_url = Constants.EMPTY_STRING;
    private String password = Constants.EMPTY_STRING;
    private String user_name = Constants.EMPTY_STRING;
}
