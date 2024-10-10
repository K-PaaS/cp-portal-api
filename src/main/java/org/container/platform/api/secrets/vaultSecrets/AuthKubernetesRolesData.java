package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

import java.util.ArrayList;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.10
 **/
@Data
public class AuthKubernetesRolesData {
    private ArrayList token_policies;
}
