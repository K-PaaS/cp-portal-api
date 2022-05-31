package org.paasta.container.platform.api.customServices.ingresses.support;

import lombok.Data;

@Data
public class IngressesRules {
    private String host;
    private IngressesHttp http;
}
