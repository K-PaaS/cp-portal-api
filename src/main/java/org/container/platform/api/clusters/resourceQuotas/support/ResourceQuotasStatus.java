package org.container.platform.api.clusters.resourceQuotas.support;

import lombok.Data;

import java.util.Map;

/**
 * ResourceQuotas Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasStatus {

    private Map<String, String> hard;
    private Map<String, String> used;

}
