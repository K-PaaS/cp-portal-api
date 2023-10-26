package org.container.platform.api.customServices.ingresses.support;

import lombok.Data;
import org.container.platform.api.common.CommonUtils;

@Data
public class IngressesRules {
    private String host;
    private IngressesHttp http;

    public Object getHost() {
        return CommonUtils.procReplaceNullValue(host);
    }


}
