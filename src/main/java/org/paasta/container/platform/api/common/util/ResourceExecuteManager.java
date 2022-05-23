package org.paasta.container.platform.api.common.util;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.exception.ContainerPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Resource Execute Manager 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.04
 **/
@Component
public class ResourceExecuteManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceExecuteManager.class);

    /**
     * Service 클래스의 Method 명 생성(Create service class's method name)
     *
     * @param kind the kind
     * @return the string
     */
    public static String makeServiceMethodName(String kind) {
        if (kind.endsWith(Constants.ENDS_WITH_S)) {
            return "create" + kind + "es";
        } else {
            return "create" + kind + "s";
        }
    }


    /**
     * Resource 값에 따라 각 Resource를 수행하는 메서드 호출(Call method according to resource value)
     *
     * @param namespace the namespace
     * @param kind the kind
     * @param yaml the yaml
     * @param isAdmin the isAdmin
     * @return the object
     * @throws Exception
     */
    public static Object execServiceMethod(String namespace, String kind, String yaml, boolean isAdmin) throws Exception {

        // get method info for processing the service class
        String [] arrMethodInfo = Constants.RESOURCE_SERVICE_MAP.get(kind).split(":");
        String methodClassName = arrMethodInfo[1].trim();
        String methodName = makeServiceMethodName(kind);

        //createServices
        LOGGER.info("method name >>> " + CommonUtils.loggerReplace(methodName));

        String injectBeanName = methodClassName.substring(0,1).toLowerCase() + methodClassName.substring(1);

        Object targetObject = InspectionUtil.getBean(injectBeanName);

        Method paramMethod = targetObject.getClass().getDeclaredMethod(methodName, String.class, String.class, boolean.class);
        if (paramMethod == null) {
            throw new ContainerPlatformException("처리할 메소드 (" + methodName + ") 가 존재 하지 않습니다.", "404");
        }

        if(namespace == null || namespace.length() == 0) {
            return paramMethod.invoke(targetObject, yaml, isAdmin);
        }

        return paramMethod.invoke(targetObject, namespace, yaml, isAdmin);
    }

    /**
     * multi yaml 순서대로 Resource 생성(Create Resource in order)
     *
     * @param namespace the namespace
     * @param yaml the yaml
     * @return the object
     * @param isAdmin the isAdmin
     * @throws Exception
     */
    public static Object commonControllerExecute(String namespace, String yaml, boolean isAdmin) throws Exception {
        String[] multiYaml;

        multiYaml = YamlUtil.splitYaml(yaml);
        Object object = null;

        for (String temp : multiYaml) {
            String kind = YamlUtil.parsingYaml(temp,"kind");
            object = execServiceMethod(namespace, kind, temp, isAdmin);
        }
        return object;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * multi yaml 순서대로 Resource 생성(Create Resource in order)
     *
     * @param namespace the namespace
     * @param yaml the yaml
     * @return the object
     * @throws Exception
     */
    public static Object commonControllerExecute(Params params) throws Exception {
        String[] multiYaml;

        multiYaml = YamlUtil.splitYaml(params.getYaml());
        Object object = null;

        for (String temp : multiYaml) {
            String kind = YamlUtil.parsingYaml(temp,"kind");
            object = execServiceMethod(params, kind);
        }
        return object;
    }


    /**
     * Resource 값에 따라 각 Resource를 수행하는 메서드 호출(Call method according to resource value)
     *
     * @param cluster the cluster
     * @param namespace the namespace
     * @param kind the kind
     * @param yaml the yaml
     * @return the object
     * @throws Exception
     */
    public static Object execServiceMethod(Params params, String kind) throws Exception {

        // get method info for processing the service class
        String [] arrMethodInfo = Constants.RESOURCE_SERVICE_MAP.get(kind).split(":");
        String methodClassName = arrMethodInfo[1].trim();
        String methodName = makeServiceMethodName(kind);

        //createServices
        LOGGER.info("method name >>> " + CommonUtils.loggerReplace(methodName));

        String injectBeanName = methodClassName.substring(0,1).toLowerCase() + methodClassName.substring(1);

        Object targetObject = InspectionUtil.getBean(injectBeanName);

        Method paramMethod = targetObject.getClass().getDeclaredMethod(methodName, String.class, String.class, boolean.class);
        if (paramMethod == null) {
            throw new ContainerPlatformException("처리할 메소드 (" + methodName + ") 가 존재 하지 않습니다.", "404");
        }

        if(params.getNamespace() == null || params.getNamespace().length() == 0) {
            return paramMethod.invoke(targetObject, params.getYaml());
        }

        return paramMethod.invoke(targetObject, params);
    }

}