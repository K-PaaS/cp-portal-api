package org.paasta.container.platform.api.clusters.resourceQuotas.support;

import lombok.Data;

/**
 * ResourceQuotas Convert Status Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasConvertStatus {
    private String used;
    private String hard;

}
