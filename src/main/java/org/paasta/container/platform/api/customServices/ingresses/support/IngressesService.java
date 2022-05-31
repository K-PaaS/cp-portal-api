package org.paasta.container.platform.api.customServices.ingresses.support;

import lombok.Data;


@Data
public class IngressesService {
    private String name;
    private IngressesPort port;


}
