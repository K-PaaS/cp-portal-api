package org.paasta.container.platform.api.clusters.nodes.support;

import lombok.Data;

/**
 * Nodes Address Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NodesAddress {
    private String address;
    private String type;
}
