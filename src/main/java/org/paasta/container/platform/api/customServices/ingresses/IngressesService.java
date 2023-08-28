package org.paasta.container.platform.api.customServices.ingresses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.apache.bcel.generic.LOOKUPSWITCH;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ingresses Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class IngressesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);

    /**
     * Instantiates a new Ingresses service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public IngressesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Ingresses 목록 조회(Get Ingresses list)
     *
     * @param params the params
     * @return the ingresses list
     */
    public IngressesList getIngressesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesListUrl(), HttpMethod.GET, null, Map.class, params);
        IngressesList ingressesList = commonService.setResultObject(responseMap, IngressesList.class);
        ingressesList = commonService.resourceListProcessing(ingressesList, params, IngressesList.class);

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pathsMap = new HashMap<>();
        Object obj = null;
        for(IngressesListItem item : ingressesList.getItems()) {

            map = item.getSpec();
            ObjectMapper objectMapper = new ObjectMapper();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                Object target = null;
                Object target2 = null;
                LOGGER.error("[key]: {}  //  [value]: {}", key, value);

                if (key.equals("rules")) {
                    List<String> result = new ArrayList<String>();

                    if(value instanceof ArrayList<?>) {
                        for (Object o : (List<?>) value){
                            Map<String, Object> map1 = objectMapper.convertValue(o, Map.class);

                            Object aa = map1.get("host");
                            String bb = aa.toString()+":30325";
                            LOGGER.error("bb = {}", bb);
                            map1.put("host", bb);
                            aa = map1.get("http");
                            Map<String, Object> httpMap = objectMapper.convertValue(aa, Map.class);


                            List<Object> pathsList = new ArrayList<Object>();

                            ObjectMapper om = new ObjectMapper();
                            for (String elementHttp : httpMap.keySet()){
                                LOGGER.error("elementHttp :: {}", elementHttp);
                                Object elementHttpObj = httpMap.get(elementHttp);
                                if(elementHttpObj instanceof ArrayList<?>) {
                                    LOGGER.error("elementHttpObj :: {}", elementHttpObj.toString());
                                    for (Object o2 : (List<?>) elementHttpObj){
                                        LOGGER.error("o2 :: {}", o2.toString());
                                        pathsMap = om.convertValue(o2, Map.class);

                                        Object c1 = pathsMap.get("path");
                                        String d1 = c1.toString().replace("(/|$)(.*)", "/");
                                        pathsMap.put("path", d1);

                                        target2 = om.convertValue(pathsMap, Object.class);
                                        LOGGER.error("target2 = {}", target2.toString());
                                        pathsList.add(target2);
                                    }
                                }
                                //map1.put("http", pathsList);
                                httpMap.put("paths", pathsList);
                            }

                            map1.put("http", httpMap);



                            target = objectMapper.convertValue(map1, Object.class);
                            LOGGER.error("target = {}", target.toString());
                        }
                    }
                    List<Object> dd = new ArrayList<Object>();
                    dd.add(target);
                    map.put(key, dd);
                }
            }

        }

        return (IngressesList) commonService.setResultModel(ingressesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses 상세 조회(Get Ingresses detail)
     *
     * @param params the params
     * @return the ingresses detail
     */
    public Ingresses getIngresses(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl(), HttpMethod.GET, null, Map.class, params);
        Ingresses ingresses = commonService.setResultObject(responseMap, Ingresses.class);
        ingresses = commonService.annotationsProcessing(ingresses, Ingresses.class);
        return (Ingresses) commonService.setResultModel(ingresses, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses YAML 조회(Get Ingresses yaml)
     *
     * @param params the params
     * @return the ingresses yaml
     */
    public CommonResourcesYaml getIngressesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses 생성(Create Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createIngresses(Params params) {

        if(params.getResourceName().equals("ingresses")){
            params.setYaml(params.getYaml().replace("metadata:", "metadata:\n  annotations:\n    nginx.ingress.kubernetes.io/rewrite-target: /"));
        }

        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses 삭제(Delete Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteIngresses(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Ingresses 수정(Update Ingresses)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateIngresses(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListIngressesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }
}














