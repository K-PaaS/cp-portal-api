package org.paasta.container.platform.api.privateRegistry;

import org.paasta.container.platform.api.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


/**
 * Private Registry Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.12.01
 */
@Service
public class PrivateRegistryService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;

    /**
     * Instantiates a new PrivateRegistry service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     */
    @Autowired
    public PrivateRegistryService(RestTemplateService restTemplateService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
    }

    /**
     * Private Registry 상세 조회(Get Private Registry detail)
     *
     * @param imageName the imageName
     * @return the private registry
     */
    public Object getPrivateRegistry(String imageName) {

        Object response = restTemplateService.sendAdmin(Constants.TARGET_COMMON_API, Constants.URI_COMMON_API_PRIVATE_REGISTRY
                .replace("{imageName:.+}", imageName), HttpMethod.GET, null, PrivateRegistry.class);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }
        return commonService.setResultModel(commonService.setResultObject(response, PrivateRegistry.class), Constants.RESULT_STATUS_SUCCESS);
    }

}
