package org.paasta.container.platform.api.clusters.support;
import lombok.Data;

/**
 * Cluster Api Access Model 클래스
 *
 * @author kjh
 * @version 1.0
 * @since 2022.12.15
 */
@Data
public class ClusterApiAccess {
   Object versions;
   Object serverAddressByClientCIDRs;
}
