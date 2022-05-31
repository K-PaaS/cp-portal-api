package org.paasta.container.platform.api.customServices.ingresses.support;

import lombok.Data;


@Data
public class IngressesPaths {
    private String path;
    private String pathType;
    private IngressesBackend backend;

}
