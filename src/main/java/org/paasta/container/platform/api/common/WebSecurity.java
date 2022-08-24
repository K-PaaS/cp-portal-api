package org.paasta.container.platform.api.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component("webSecurity")
public class WebSecurity {

    @Autowired
    private CommonService commonService;

    public boolean checkisGlobalAdmin() {
        return !commonService.getGlobalAuthority().isEmpty() && !commonService.getGlobalAuthority().equals(Constants.AUTH_USER);
    }

    public boolean checkisSuperAdmin() {
        return commonService.getGlobalAuthority().equals(Constants.AUTH_SUPER_ADMIN);
    }

    public boolean checkClusterAdmin(String clusterId) {
        return !commonService.getClusterAuthorityFromContext(clusterId).isEmpty() &&
                !commonService.getClusterAuthorityFromContext(clusterId).equals(Constants.AUTH_USER);
    }

    public boolean checkAuthority(String role) {
        return commonService.getGlobalAuthority().equals(role);
    }
}
