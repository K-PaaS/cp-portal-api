package org.paasta.container.platform.api.common.util;

import org.apache.commons.lang3.StringUtils;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Inspection Util 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 **/
public class InspectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionUtil.class);


    /**
     * Bean 주입(Inject Bean)
     *
     * @param bean the bean
     * @return the object
     */
    public static Object getBean(String bean) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(bean);
    }



    /**
     * PropertyService 의 create, update 메소드 명 생성(Make method name with field name)
     *
     * @param fieldName the field name
     * @param suffix the suffix
     * @return the string
     */
    public static String makeMethodName(String fieldName, String suffix) {
        if (fieldName.endsWith(Constants.ENDS_WITH_S)) {
            return "getCpMasterApiList" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "es" + suffix;
        } else {
            return "getCpMasterApiList" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "s" + suffix;
        }
    }


    /**
     * Resource 명 생성(Make resource name)
     * ex) deployments -> deployment
     *
     * @param resourceName the resource name
     * @return the string
     */
    public static String makeResourceName(String resourceName) {
        if (resourceName.endsWith(Constants.ENDS_WITH_SES)) {
            return resourceName.substring(0, resourceName.length()-2).toLowerCase();
        } else {
            return resourceName.substring(0, resourceName.length()-1).toLowerCase();
        }
    }


    /**
     * requestURI 파싱(Parsing requestURI)
     * ex) /namespaces/cp-namespace/deployments, /namespaces/cp-namespace/deployments/deploymentsName
     *
     * @param requestURI the request uri
     * @return the string[]
     */
    public static String[] parsingRequestURI(String requestURI) {

        String[] pathArray = requestURI.split("/");

        return pathArray;
    }


    /**
     * dryRun 체크를 위한 동적 API URL Call 메서드 조회(Get Automatically API URL for dryRun check)
     *
     * @param methodType the method type
     * @param kind the kind
     * @return the string
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static String verifyMethodCall(String methodType, String kind) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = makeMethodName(kind.trim(), methodType);
        PropertyService propertyService = (PropertyService) getBean("propertyService");

        // 해당 Resource의 method 이름
        Method method = propertyService.getClass().getDeclaredMethod(methodName);
        LOGGER.info("Method Name >>> " + CommonUtils.loggerReplace(methodName));

        // 동적 K8s API Endpoint
        Object recursiveObj = method.invoke(propertyService);
        String finalUrl = recursiveObj.toString();
        LOGGER.info("K8s API Endpoint >>> " + CommonUtils.loggerReplace(finalUrl));

        return finalUrl;
    }

    /**
     * CREATE/UPDATE dryRun 체크(Check DryRun)
     *
     * @param namespace the namespace
     * @param kind the kind
     * @param yaml the yaml
     * @param resourceName the resource name
     * @return the object
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object resourceDryRunCheck(String methodType, String namespace, String kind, String yaml, String resourceName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RestTemplateService restTemplateService = (RestTemplateService) getBean("restTemplateService");
        String finalUrl = verifyMethodCall(methodType, kind);

        if (StringUtils.isNotEmpty(namespace)) {
            if (StringUtils.isEmpty(resourceName)) {
                return restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,  finalUrl.replace("{namespace}", namespace) + "?dryRun=All", HttpMethod.POST, yaml, Map.class, true);
            } else {
                return restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,  finalUrl.replace("{namespace}", namespace).replace("{name}", resourceName) + "?dryRun=All", HttpMethod.PUT, yaml, Map.class, true);
            }
        } else {
            if (StringUtils.isEmpty(resourceName)) {
                return restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,  finalUrl + "?dryRun=All", HttpMethod.POST, yaml, Map.class, true);
            } else {
                return restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,  finalUrl.replace("{name}", resourceName) + "?dryRun=All", HttpMethod.PUT, yaml, Map.class, true);
            }
        }

    }
}
