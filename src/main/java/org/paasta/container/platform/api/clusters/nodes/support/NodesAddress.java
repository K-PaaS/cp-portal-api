package org.paasta.container.platform.api.clusters.nodes.support;

import lombok.Data;

/**
 * Nodes Address Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.31
 */
@Data
public class NodesAddress {
    private String address;
    private String type;
}
