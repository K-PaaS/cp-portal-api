package org.paasta.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

@Data
public class TerramanParams {
    String cluster_id = Constants.EMPTY_STRING;
    String provider = Constants.EMPTY_STRING;
    int seq;
}
