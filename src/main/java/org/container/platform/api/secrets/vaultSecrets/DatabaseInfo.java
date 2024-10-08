package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class DatabaseInfo {
    private String dbType;
    private String name;
    private String createdTime;
}
