package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

@Data
public class PortalRequestParams {
    String tag = Constants.EMPTY_STRING;
    String cluster = Constants.EMPTY_STRING;
    String namespace = Constants.EMPTY_STRING;
    String resourceName = Constants.EMPTY_STRING;
}
