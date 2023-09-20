package org.container.platform.api.customServices.ingresses.support;

import lombok.Data;

import java.util.List;


@Data
public class IngressesSpec {
    private List<IngressesRules> rules;
}
