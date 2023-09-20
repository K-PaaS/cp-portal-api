package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

@Data
public class TerramanParams {
    String clusterId = Constants.EMPTY_STRING;
    String provider = Constants.EMPTY_STRING;
    String seq = Constants.EMPTY_STRING;
}
