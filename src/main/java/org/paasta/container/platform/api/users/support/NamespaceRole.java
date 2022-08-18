package org.paasta.container.platform.api.users.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Namespace Role Model 클래스
 *
 * @author kjh
 * @version 1.0
 * @since 2022.08.18
 */

@Data
@NoArgsConstructor
public class NamespaceRole {
    private String namespace;
    private String role;


    public NamespaceRole(String namespace, String role) {
        this.namespace = namespace;
        this.role = role;
    }
}

