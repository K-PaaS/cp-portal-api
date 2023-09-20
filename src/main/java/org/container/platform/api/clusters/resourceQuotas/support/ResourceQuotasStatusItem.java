package org.container.platform.api.clusters.resourceQuotas.support;

import lombok.Data;

/**
 * ResourceQuotas Status Item Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasStatusItem {

    private String resource;
    private String hard;
    private String used;

    public ResourceQuotasStatusItem(String resource, String hard, String used) {
        this.resource = resource;
        this.hard = hard;
        this.used = used;
    }
}
