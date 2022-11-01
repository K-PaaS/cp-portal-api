package org.paasta.container.platform.api.clusters.hclTemplates;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.exception.ResultStatusException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * HclTemplates Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/

@Service
public class HclTemplatesService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;

    /**
     * Instantiates a new HclTemplates service
     *
     * @param restTemplateService the rest template service
     * @param commonService the common service
     */
    HclTemplatesService(RestTemplateService restTemplateService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
    }


    /**
     * HclTemplates 목록 조회(Get HclTemplates List)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplatesList getHclTemplatesList(Params params) {
        HclTemplatesList hclTemplatesList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.GET, null, HclTemplatesList.class, params);
        hclTemplatesList = commonService.globalListProcessing(hclTemplatesList, params, HclTemplatesList.class);
        return (HclTemplatesList) commonService.setResultModel(hclTemplatesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 타입 별 목록 조회(Get HclTemplates List By Provider)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplatesList getHclTemplatesListByProvider(Params params) {
        HclTemplatesList hclTemplatesList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/provider/{providerType:.+}"
                .replace("{providerType:.+}", params.getProviderType().name()), HttpMethod.GET, null, HclTemplatesList.class, params);
        hclTemplatesList = commonService.globalListProcessing(hclTemplatesList, params, HclTemplatesList.class);
        return (HclTemplatesList) commonService.setResultModel(hclTemplatesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 조회(Get HclTemplates)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplates getHclTemplates(Params params) {
        HclTemplates hclTemplates = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.GET, null, HclTemplates.class, params);
        return (HclTemplates)commonService.setResultModel(hclTemplates, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 생성(Create HclTemplates)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplates createHclTemplates(Params params){
        HclTemplates hclTemplates = setHclTemplates(params);
        if (ObjectUtils.isEmpty(hclTemplates.getName())) {
            throw new ResultStatusException(MessageConstant.INVALID_NAME_FORMAT.getMsg());
        }
        return (HclTemplates) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.POST, hclTemplates, HclTemplates.class, params), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 수정(Update HclTemplates)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplates updateHclTemplates(Params params) {
        HclTemplates hclTemplates = getHclTemplates(params);
        params.setResourceUid(Long.toString(hclTemplates.getId()));
        return (HclTemplates) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates", HttpMethod.PUT, setHclTemplates(params), HclTemplates.class, params), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 삭제(Delete HclTemplates)
     *
     * @param params the params
     * @return the hclTemplates
     */
    HclTemplates deleteHclTemplates(Params params) {
        return (HclTemplates) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/hclTemplates/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.DELETE, setHclTemplates(params), HclTemplates.class, params),
                Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * HclTemplates 설정(Set HclTemplates)
     *
     * @param params the params
     * @return the hclTemplates
     */
    private HclTemplates setHclTemplates(Params params){
        HclTemplates hclTemplates = new HclTemplates();
        if(!params.getResourceUid().equals(Constants.EMPTY_STRING))
            hclTemplates.setId(Long.parseLong(params.getResourceUid()));
        hclTemplates.setName(params.getResourceName());
        hclTemplates.setProvider(params.getProviderType().name());
        hclTemplates.setHclScript(params.getHclScript());
        hclTemplates.setRegion(params.getRegion());
        return  hclTemplates;
    }


}
