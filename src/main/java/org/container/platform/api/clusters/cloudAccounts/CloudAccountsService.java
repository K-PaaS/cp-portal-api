package org.container.platform.api.clusters.cloudAccounts;


import org.container.platform.api.clusters.clusters.support.AWSInfo;
import org.container.platform.api.clusters.clusters.support.NAVERInfo;
import org.container.platform.api.clusters.clusters.support.NHNInfo;
import org.container.platform.api.clusters.clusters.support.OpenstackInfo;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.exception.ResultStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * CloudAccounts Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Service
public class CloudAccountsService {
    private final RestTemplateService restTemplateService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudAccountsService.class);


    /**
     * Instantiates a new Clusters service
     *
     * @param restTemplateService the rest template service
     * @param vaultService        the vault service
     * @param propertyService     the property service
     * @param commonService       the common service
     */
    CloudAccountsService(RestTemplateService restTemplateService, VaultService vaultService, PropertyService propertyService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.commonService = commonService;
    }

    /**
     * CloudAccounts 정보 저장(Create CloudAccounts)
     *
     * @param params the params
     * @return the CloudAccounts
     */
    public CloudAccounts createCloudAccounts(Params params) {
        CloudAccounts cloudAccounts = setCloudAccounts(params);
        if (ObjectUtils.isEmpty(cloudAccounts.getName())) {
            throw new ResultStatusException(MessageConstant.INVALID_NAME_FORMAT.getMsg());
        }

        CloudAccounts ret = new CloudAccounts();
        try {
            ret = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.POST, cloudAccounts, CloudAccounts.class, params);
            vaultService.write(propertyService.getCpVaultPathProviderCredential()
                            .replace("{iaas}", cloudAccounts.getProvider())
                            .replace("{id}", "" + ret.getId()),
                    getProviderInfo(params));
        } catch (Exception e) {
            LOGGER.info("vault write failed");
            if(ret != null) {
                restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id}".replace("{id}", String.valueOf(ret.getId())), HttpMethod.DELETE, null, CloudAccounts.class, params);
            }
            throw new ResultStatusException(MessageConstant.INVALID_NAME_FORMAT.getMsg());

        }
        return (CloudAccounts) commonService.setResultModel(ret, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * CloudAccounts 목록 조회 (Get CloudAccounts List)
     *
     * @param params the params
     * @return the CloudAccountsList
     */
    public CloudAccountsList getCloudAccountsList(Params params) {
        CloudAccountsList cloudAccountsList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.GET, null, CloudAccountsList.class, params);
        cloudAccountsList = commonService.globalListProcessing(cloudAccountsList, params, CloudAccountsList.class);
        return (CloudAccountsList) commonService.setResultModel(cloudAccountsList, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * CloudAccounts 타입 별 목록 조회 (Get CloudAccounts List By Provider)
     *
     * @param params the params
     * @return the CloudAccountsList
     */
    public CloudAccountsList getCloudAccountsListByProvider(Params params) {
        CloudAccountsList cloudAccountsList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/provider/{providerType:.+}"
                .replace("{providerType:.+}", params.getProviderType().name()), HttpMethod.GET, null, CloudAccountsList.class, params);
        cloudAccountsList = commonService.globalListProcessing(cloudAccountsList, params, CloudAccountsList.class);
        return (CloudAccountsList) commonService.setResultModel(cloudAccountsList, Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * CloudAccounts 조회 (Get CloudAccounts)
     *
     * @param params the params
     * @return the CloudAccounts
     */
    public CloudAccounts getCloudAccounts(Params params) {
        CloudAccounts cloudAccounts = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.GET, null, CloudAccounts.class, params);
        if (cloudAccounts.getResultCode() != null && cloudAccounts.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {
            return cloudAccounts;
        }

        params.setProviderType(Constants.ProviderType.valueOf(cloudAccounts.getProvider()));
        LOGGER.info("providerType : " + CommonUtils.loggerReplace(cloudAccounts.getProvider()));
        cloudAccounts.setProviderInfo(getProviderInfoFromVault(params)); //FIXME 예외처리

        return (CloudAccounts) commonService.setResultModel(cloudAccounts, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * CloudAccounts 수정 (Update CloudAccounts)
     *
     * @param params the params
     * @return the CloudAccounts
     */
    public CloudAccounts updateCloudAccounts(Params params) {
        //check id
        CloudAccounts cloudAccounts = setCloudAccounts(params);
        if (cloudAccounts == null) {
            CloudAccounts ret = new CloudAccounts();
            ret.setResultCode(Constants.RESULT_STATUS_FAIL);
            ret.setDetailMessage("Invalid id");
            return ret;
        }

        return (CloudAccounts) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts", HttpMethod.PATCH, cloudAccounts, CloudAccounts.class, params), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * CloudAccounts 삭제 (Delete CloudAccounts)
     *
     * @param params the params
     * @return the CloudAccounts
     */
    public CloudAccounts deleteCloudAccounts(Params params) {
        String caPath = "";
        CloudAccounts getCA = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.GET, null, CloudAccounts.class, params);

        if(getCA != null) {
            caPath = propertyService.getCpVaultPathProviderCredential()
                    .replace("{iaas}", getCA.getProvider()).replace("{id}", params.getResourceUid());
        }

        //vault delete
        vaultService.delete(caPath);

        CloudAccounts cloudAccounts = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/cloudAccounts/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.DELETE, null, CloudAccounts.class, params);
        if (cloudAccounts.getResultCode() != null && cloudAccounts.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {
            return cloudAccounts;
        }
        return (CloudAccounts) commonService.setResultModel(cloudAccounts, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ProviderInfo 조회 (get ProviderInfo)
     *
     * @param params the params
     * @return the Object
     */
    public Object getProviderInfo(Params params) {
        Map providerInfoList = getProviderInfoList(params).getItems();
        if (providerInfoList.containsKey(params.getProviderType().name())) {
            return commonService.setResultObject(params.getProviderInfo(), providerInfoList.get(params.getProviderType().name()).getClass());
        } else return null;
    }

    /**
     * ProviderInfo 목록 조회 (get ProviderInfo List)
     *
     * @param params the params
     * @return the Object
     */
    public ProviderInfoList getProviderInfoList(Params params) {
        final String providerInfoPath = "org.container.platform.api.clusters.clusters.support";
        ProviderInfoList providerInfoList = new ProviderInfoList();
        Map<String, Object> ret = new HashMap<>();
        for (Constants.ProviderType p : Constants.ProviderType.values()) {
            try {
                Class<?> classType = Class.forName(providerInfoPath + "." + p.getClassType());
                ret.put(p.name(), classType.newInstance());
            } catch (Exception e) {
                LOGGER.info("Invalid ClassName: " + CommonUtils.loggerReplace(e.getMessage()));
            }
        }

        providerInfoList.setItems(ret);
        providerInfoList.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        return providerInfoList;
    }

    /**
     * Vault로부터 ProviderInfo 정보 조회 (get ProviderInfo from Vault)
     *
     * @param params the params
     * @return the Object
     */
    public Object getProviderInfoFromVault(Params params) {
        Object ret;
        String path = propertyService.getCpVaultPathProviderCredential()
                .replace("{iaas}", params.getProviderType().name()).replace("{id}", params.getResourceUid());
        try {
            ret = vaultService.read(path, ((Map) (getProviderInfoList(params))).get(params.getProviderType().name()).getClass());
        } catch (Exception e) {
            LOGGER.info("Error from getProviderInfoFromVault!");
            return null;
        }

        return ret;
    }

    public CloudAccounts setCloudAccounts(Params params) {
        CloudAccounts cloudAccounts = new CloudAccounts();
        //FIXME! uid 검사, 유효한 값 검사
        if (params.getResourceUid() != null && params.getResourceUid() != "")
            cloudAccounts.setId(Long.parseLong(params.getResourceUid()));
        cloudAccounts.setName(params.getResourceName());
        cloudAccounts.setProvider(params.getProviderType().name());
        cloudAccounts.setRegion(params.getRegion());
        if (cloudAccounts.getProvider().equals(Constants.ProviderType.OPENSTACK.name()) && getProviderInfo(params) != null) {
            cloudAccounts.setProject(params.getProject());
        }
        if (cloudAccounts.getProvider().equals(Constants.ProviderType.NHN.name()) && getProviderInfo(params) != null) {
            cloudAccounts.setProject(params.getProject());
        }
        if (cloudAccounts.getProvider().equals(Constants.ProviderType.NAVER.name()) && getProviderInfo(params) != null) {
            cloudAccounts.setSite(params.getSite());
        }
//        if (cloudAccounts.getProvider().equals(Constants.ProviderType.NAVER.name()) && getProviderInfo(params) != null) {
//            cloudAccounts.setProject(params.getProject());
//        }
     /*   if(cloudAccounts.getProvider().equals(Constants.ProviderType.GCP.name()) && getProviderInfo(params) != null) {
            GCPInfo gcpInfo = (GCPInfo) getProviderInfo(params);
            cloudAccounts.setProject(gcpInfo.getProject_name());
        }*/
        return cloudAccounts;
    }

    // Cloud Account 등록 시 필수 사항 누락 check
    public void validationCheckCloudAccounts(Params params) {
        String provider = params.getProviderType().name();
        if (params.getResourceName().equals(Constants.EMPTY_STRING) || params.getRegion().equals(Constants.EMPTY_STRING)) {
            throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
        }
        if (provider.equalsIgnoreCase(Constants.ProviderType.OPENSTACK.name())) {
            OpenstackInfo openstackInfo = commonService.setResultObject(params.getProviderInfo(), OpenstackInfo.class);
            if (params.getProject().equals(Constants.EMPTY_STRING) ||
                    openstackInfo.getAuth_url().equals(Constants.EMPTY_STRING) ||
                    openstackInfo.getPassword().equals(Constants.EMPTY_STRING) ||
                    openstackInfo.getUser_name().equals(Constants.EMPTY_STRING)) {
                throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
            }
        }
        else if (provider.equalsIgnoreCase(Constants.ProviderType.NHN.name())) {
            NHNInfo nhnInfo = commonService.setResultObject(params.getProviderInfo(), NHNInfo.class);
            if (params.getProject().equals(Constants.EMPTY_STRING) ||
                    nhnInfo.getAuth_url().equals(Constants.EMPTY_STRING) ||
                    nhnInfo.getPassword().equals(Constants.EMPTY_STRING) ||
                    nhnInfo.getUser_name().equals(Constants.EMPTY_STRING)) {
                throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
            }
        }
            else if (provider.equalsIgnoreCase(Constants.ProviderType.NAVER.name())) {
                NAVERInfo naverInfo = commonService.setResultObject(params.getProviderInfo(), NAVERInfo.class);
                if (params.getSite().equals(Constants.EMPTY_STRING) ||
                        naverInfo.getAccessKey().equals(Constants.EMPTY_STRING) ||
                        naverInfo.getSecretKey().equals(Constants.EMPTY_STRING)) {
                    throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());

                }
            } else {
                AWSInfo awsInfo = commonService.setResultObject(params.getProviderInfo(), AWSInfo.class);
                if (awsInfo.getAccessKey().equals(Constants.EMPTY_STRING) ||
                     awsInfo.getSecretKey().equals(Constants.EMPTY_STRING)) {
                    throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
                }

        }
        }
    }


