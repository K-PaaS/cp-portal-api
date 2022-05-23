package org.paasta.container.platform.api.adminToken;

import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Admin Token Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.30
 **/
@Service
public class AdminTokenService {
    private final RestTemplateService restTemplateService;


    /**
     * Instantiates a new Admin Token Service service
     *
     * @param restTemplateService the rest template service
     */
    @Autowired
    public AdminTokenService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }


    /**
     * Admin Token 저장(Save Admin Token)
     *
     * @param bearerToken the bearer token
     * @return the adminToken
     */
    public AdminToken saveAdminToken(String bearerToken) {
        AdminToken adminToken = new AdminToken();
        adminToken.setTokenName(Constants.TOKEN_KEY);
        adminToken.setTokenValue(bearerToken);

        return restTemplateService.send(Constants.TARGET_COMMON_API, "/adminToken", HttpMethod.POST, adminToken, AdminToken.class);
    }
}
