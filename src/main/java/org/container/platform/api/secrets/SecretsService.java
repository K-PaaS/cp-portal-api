package org.container.platform.api.secrets;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Secrets Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/
@Service
public class SecretsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private final ResourceYamlService resourceYamlService;
    private final VaultService vaultService;

    /**
     * Instantiates a new Secrets service
     *
     * @param restTemplateService  the rest template service
     * @param commonService        the common service
     * @param propertyService      the property service
     * @param resourceYamlService  the resource yaml service
     */
    @Autowired
    public SecretsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, ResourceYamlService resourceYamlService, VaultService vaultService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.resourceYamlService = resourceYamlService;
        this.vaultService = vaultService;
    }

    /**
     * Secrets 목록 조회(Get Secrets List)
     *
     * @param params the params
     * @return the Secrets list
     */
    public SecretsList getSecretsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsListUrl(), HttpMethod.GET, null, Map.class, params);
        return procSecretsList(responseMap, params);
    }

    /**
     * Secrets 상세 조회(Get Secrets Detail)
     *
     * @param params the params
     * @return the Secrets detail
     */
    public Secrets getSecrets(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, params);
        Secrets secrets = commonService.setResultObject(responseMap, Secrets.class);
        secrets = commonService.annotationsProcessing(secrets, Secrets.class);
        return (Secrets) commonService.setResultModel(secrets, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secrets YAML 조회(Get Secrets Yaml)
     *
     * @param params the params
     * @return the Secrets yaml
     */
    public CommonResourcesYaml getSecretsYaml(Params params){
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * Secrets 생성(Create Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createSecrets(Params params) {

        if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_KUBERNETES)) {
            resourceYamlService.createSecrets(params);
        } else if (params.getStorageBackend().equals(Constants.STORAGE_BACK_END_VAULT)) {
            vaultService.createVaultSecretsEnginesDatabase(params);
        }

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secrets 삭제(Delete Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteSecrets(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Secrets 수정(Update Secrets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateSecrets(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsGetUrl(), HttpMethod.GET, null, Map.class, params);
        Secrets secrets = commonService.setResultObject(responseMap, Secrets.class);
        secrets = commonService.annotationsProcessing(secrets, Secrets.class);

        resourceYamlService.updateSecrets(params, secrets);

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListSecretsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 필터링된 Secrets 목록 조회(Get filtered Secrets list)
     *
     * @param responseMap the HashMap
     * @param params the params
     * @return the filtered Secrets list
     */
    SecretsList procSecretsList(HashMap responseMap, Params params){
        SecretsList secretsList = commonService.setResultObject(responseMap, SecretsList.class);
        secretsList.setItems(secretsList.getItems().stream()
                .filter(x -> !x.getMetadata().getName().contains(Constants.DEFAULT_SECRETS))
                .collect(Collectors.toList()));
        secretsList = commonService.resourceListProcessing(secretsList, params, SecretsList.class);
        return (SecretsList) commonService.setResultModel(secretsList, Constants.RESULT_STATUS_SUCCESS);
    }
}
