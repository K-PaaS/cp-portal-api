package org.container.platform.api.customServices.ingresses.support;

import lombok.Data;

import java.util.List;


@Data
public class IngressesHttp {
    private List<IngressesPaths> paths;
}
