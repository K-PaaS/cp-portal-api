package org.container.platform.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.*;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


/**
 * VaultConfig 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2025-02-13
 */

@EnableConfigurationProperties(VaultProperties.class)
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {
    private final VaultProperties vaultProperties;
    public VaultConfig(VaultProperties vaultProperties) {
        this.vaultProperties = vaultProperties;
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        try {
            return VaultEndpoint.from(new URI(Objects.requireNonNull(vaultProperties.getUri())));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ClientAuthentication clientAuthentication() {
        VaultProperties.AppRoleProperties appRole = vaultProperties.getAppRole();
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .appRole(appRole.getRole())
                .roleId(AppRoleAuthenticationOptions.RoleId.provided(Objects.requireNonNull(appRole.getRoleId())))
                .secretId(AppRoleAuthenticationOptions.SecretId.provided(Objects.requireNonNull(appRole.getSecretId())))
                .build();
        return new AppRoleAuthentication(options, restOperations());
    }
}
