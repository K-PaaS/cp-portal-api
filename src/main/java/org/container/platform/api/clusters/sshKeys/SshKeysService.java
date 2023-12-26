package org.container.platform.api.clusters.sshKeys;

import org.container.platform.api.clusters.cloudAccounts.ProviderInfoList;
import org.container.platform.api.clusters.sshKeys.support.SshKeysInfo;
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
 * SshKeys Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.01.04
 **/
@Service
public class SshKeysService {

    private final RestTemplateService restTemplateService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SshKeysService.class);

    /**
     * Instantiates a new SshKeys service
     *
     * @param restTemplateService the rest template service
     * @param vaultService        the vault service
     * @param propertyService     the property service
     * @param commonService the common service
     */
    SshKeysService(RestTemplateService restTemplateService, VaultService vaultService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.commonService = commonService;
    }

    /**
     * SshKeys 조회(Get SshKeys)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeys getSshKeys(Params params) {
        SshKeys sshKeys = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.GET, null, SshKeys.class, params);

        String path = propertyService.getVaultBase();
        path =  path + Constants.DIR_SSH_KEY + params.getResourceUid();
        HashMap<String, Object> res = vaultService.read(path, HashMap.class);

        String privateKey = "";
        privateKey = res != null ? String.valueOf(res.get("privateKey")) : "";

        sshKeys.setPrivateKey(privateKey);

        if (sshKeys.getResultCode() != null && sshKeys.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {
            return sshKeys;
        }

        return (SshKeys)commonService.setResultModel(sshKeys, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 목록 조회(Get SshKeys List)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeysList getSshKeysList(Params params) {
        SshKeysList sshKeysList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.GET, null, SshKeysList.class, params);
        sshKeysList = commonService.globalListProcessing(sshKeysList, params, SshKeysList.class);
        return (SshKeysList) commonService.setResultModel(sshKeysList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * SshKeys 타입 별 목록 조회(Get SshKeys List By Provider)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeysList getSshKeysListByProvider(Params params) {
        SshKeysList sshKeysList = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/provider/{providerType:.+}"
                .replace("{providerType:.+}", params.getProviderType().name()), HttpMethod.GET, null, SshKeysList.class, params);
        sshKeysList = commonService.globalListProcessing(sshKeysList, params, SshKeysList.class);
        return (SshKeysList) commonService.setResultModel(sshKeysList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * SshKeys 생성(Create SshKeys)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeys createSshKeys(Params params) {
        SshKeys sshKeys = setSshKeys(params);
        SshKeysInfo sshKeysInfo = new SshKeysInfo();
        sshKeysInfo.setPrivateKey(params.getPrivateKey());

        //이름 중복 체크
        boolean nameCheck = true;
        if (sshKeys.getName().isEmpty()) {
            throw new ResultStatusException(MessageConstant.SET_SSH_KEY_NAME.getMsg());
        } else {
            SshKeysList sshKeysList = getSshKeysList(params);
            for (SshKeys keyName : sshKeysList.getItems()) {
                if (keyName.getName().equals(params.getResourceName())) {
                    nameCheck = false;
                    throw new ResultStatusException(MessageConstant.DUPLICATE_SSH_KEY.getMsg());
                }
            }
        }

        if (ObjectUtils.isEmpty(sshKeysInfo.getPrivateKey())) {
            throw new ResultStatusException(MessageConstant.UPLOAD_SSH_KEY.getMsg());
        }

        if (nameCheck == true) {
            SshKeys ret = new SshKeys();
            try {
                ret = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.POST, sshKeys, SshKeys.class, params);
                vaultService.write(propertyService.getCpVaultPathSshKey().replace("{id}", "" + ret.getId()), sshKeysInfo);
            } catch (Exception e) {
                LOGGER.info("vault write failed");
                if(ret != null) {
                    restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id}".replace("{id}", String.valueOf(ret.getId())), HttpMethod.DELETE, null, SshKeys.class, params);
                }
                throw new ResultStatusException(MessageConstant.INVALID_NAME_FORMAT.getMsg());

            }
            return (SshKeys) commonService.setResultModel(ret, Constants.RESULT_STATUS_SUCCESS);
        } else {
            return (SshKeys) commonService.setResultModel(sshKeys, Constants.RESULT_STATUS_FAIL);
        }


    }

    /**
     * SshKeys 수정(Update SshKeys)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeys modifyInitSshKeys(Params params) {
        return (SshKeys) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.PUT, setSshKeys(params), SshKeys.class, params), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 삭제(Delete SshKeys)
     *
     * @param params the params
     * @return the sshKeys
     */
    public SshKeys deleteSshKeys(Params params) {
        String skPath = "";
        SshKeys getSK = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.GET, null, SshKeys.class, params);

        if(getSK != null) {
            skPath = propertyService.getCpVaultPathSshKey().replace("{id}", params.getResourceUid());
        }

        vaultService.delete(skPath);

        SshKeys sshKeys = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                .replace("{id:.+}", params.getResourceUid()), HttpMethod.DELETE, null, SshKeys.class, params);
        if (sshKeys.getResultCode() != null && sshKeys.getResultCode().equals(Constants.RESULT_STATUS_FAIL)) {
            return sshKeys;
        }
        return (SshKeys) commonService.setResultModel(sshKeys, Constants.RESULT_STATUS_SUCCESS);
      /*  return (SshKeys) commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                        .replace("{id:.+}", params.getResourceUid()), HttpMethod.DELETE, setSshKeys(params), SshKeys.class, params),
                Constants.RESULT_STATUS_SUCCESS);*/
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
     * SshKeys 설정(Set SshKeys)
     *
     * @param params the params
     * @return the sshKeys
     */
    private SshKeys setSshKeys(Params params){
        SshKeys sshKeys = new SshKeys();
        if(!params.getResourceUid().equals(Constants.EMPTY_STRING))
            sshKeys.setId(Long.parseLong(params.getResourceUid()));
        sshKeys.setName(params.getResourceName());
        sshKeys.setProvider(params.getProviderType().name());
        return sshKeys;
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
}
