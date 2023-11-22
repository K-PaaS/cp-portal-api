package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

@Data
public class NHNInfo {
    private String auth_url = Constants.EMPTY_STRING;
    private String password = Constants.EMPTY_STRING;
    private String user_name = Constants.EMPTY_STRING;
}
