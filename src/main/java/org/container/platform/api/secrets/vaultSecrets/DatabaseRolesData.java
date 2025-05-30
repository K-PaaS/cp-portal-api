package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseRoles 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class DatabaseRolesData {
    private String default_ttl;
    private String max_ttl;
}
