package org.paasta.container.platform.api.accessInfo;

import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Access Token Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.29
 */
@Service
public class AccessTokenService {
    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final CommonService commonService;

    /**
     * Instantiates a new AccessToken service
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     * @param commonService       the common service
     */
    @Autowired
    public AccessTokenService(RestTemplateService restTemplateService, PropertyService propertyService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.commonService = commonService;
    }

    /**
     * Secrets 상세 조회(Get Secrets detail)
     *
     * @param namespace the namespace
     * @param accessTokenName the accessTokenName
     * @return the accessToken detail
     */
    public AccessToken getSecrets(String namespace, String accessTokenName) {
        String caCertToken;
        String userToken;

        HashMap responseMap = (HashMap) restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", accessTokenName), HttpMethod.GET, null, Map.class);

        Map map = (Map) responseMap.get("data");

        caCertToken = map.get("ca.crt").toString();
        userToken = map.get("token").toString();

        Base64.Decoder decoder = Base64.getDecoder();
        String caCertDecodeToken = new String(decoder.decode(caCertToken));
        String userDecodeToken = new String(decoder.decode(userToken));

        AccessToken accessToken = new AccessToken();
        accessToken.setCaCertToken(caCertDecodeToken);
        accessToken.setUserAccessToken(userDecodeToken);

        return (AccessToken) commonService.setResultModel(commonService.setResultObject(accessToken, AccessToken.class), Constants.RESULT_STATUS_SUCCESS);
    }

}
