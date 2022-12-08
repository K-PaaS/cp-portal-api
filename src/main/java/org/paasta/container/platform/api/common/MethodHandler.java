package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.common.util.InspectionUtil;
import org.paasta.container.platform.api.common.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

import static org.paasta.container.platform.api.common.Constants.NOT_ALLOWED_POD_NAME_LIST;

/**
 * Method Handler 클래스
 * AOP - Common Create/Update resource
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 **/
@Aspect
@Component
@Order(1)
public class MethodHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandler.class);
    private static final String IS_ADMIN_KEY = "isAdmin";
    private static final String YAML_KEY = "yaml";
    private static final String NAMESPACE_KEY = "namespace";
    private static final String KIND_KEY = "kind";
    private static final String METADATA_KEY = "metadata";
    private static final String METADATA_NAME_KEY = "name";

    private final HttpServletRequest request;
    private final PropertyService propertyService;

    @Autowired
    public MethodHandler(HttpServletRequest request, PropertyService propertyService) {
        this.request = request;
        this.propertyService = propertyService;
    }


    /**
     * API URL 호출 시 create 메소드인 경우 메소드 수행 전 처리 (do preprocessing, if create method is)
     *
     * @param joinPoint the joinPoint
     * @throws Throwable
     */
    @Around("execution(* org.paasta.container.platform.api..*Controller.*create*(..))"
            + "&& !execution(* org.paasta.container.platform.api.clusters.cloudAccounts.*.*(..))"
            + "&& !execution(* org.paasta.container.platform.api.clusters.clusters.*.*(..))"
            + "&& !execution(* org.paasta.container.platform.api.clusters.hclTemplates.*.*(..))")
    public Object createResourceAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        String yaml = "";
        String namespace = "";
        Boolean isAdmin = true;

        Object[] parameterValues = Arrays.asList(joinPoint.getArgs()).toArray();
        Params params = (Params) parameterValues[0];

        namespace = params.getNamespace();


        if(namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NAMESPACES_CANNOT_BE_CREATED.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NAMESPACES_CANNOT_BE_CREATED.getMsg());
        }

        String requestResource;
        String requestURI = request.getRequestURI();
        String resource;

        if (StringUtils.isEmpty(namespace)) {
            requestResource = InspectionUtil.parsingRequestURI(requestURI)[3];
        } else {
            requestResource = InspectionUtil.parsingRequestURI(requestURI)[5];
        }

        resource = InspectionUtil.parsingRequestURI(requestURI)[5];
        params.setResource(resource);

        if (StringUtils.isEmpty(yaml)) {
            YamlUtil.makeResourceYaml(params);
        }

        yaml = params.getYaml();

        requestResource = InspectionUtil.makeResourceName(requestResource);

        LOGGER.info("Creating Request Resource :: " + CommonUtils.loggerReplace(requestResource));

        String[] yamlArray = YamlUtil.splitYaml(yaml);
        boolean isExistResource = false;

        for (String temp : yamlArray) {
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String createYamlResourceName = YamlMetadata.get(METADATA_NAME_KEY).toString();
            String createYamlResourceNamespace;

            if (YamlMetadata.get(NAMESPACE_KEY) != null) {
                createYamlResourceNamespace = YamlMetadata.get(NAMESPACE_KEY).toString();
            } else {
                createYamlResourceNamespace = null;
            }

            if (StringUtils.isNotEmpty(createYamlResourceName) && StringUtils.isNotEmpty(createYamlResourceNamespace)) {
                if (createYamlResourceName.startsWith("kube")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                } else {
                    break;
                }
            } else if (StringUtils.isNotEmpty(createYamlResourceName) && StringUtils.isEmpty(createYamlResourceNamespace)) {
                if (createYamlResourceName.startsWith("kube")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                } else {
                    break;
                }
            }
        }

        for (String temp : yamlArray) {
            String YamlKind = YamlUtil.parsingYaml(temp, KIND_KEY);
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);

            String createYamlResourceName = YamlMetadata.get(METADATA_NAME_KEY).toString();

            if (YamlKind.equals(Constants.RESOURCE_POD)) {
                for (String na : NOT_ALLOWED_POD_NAME_LIST) {
                    if (createYamlResourceName.equals(na)) {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_POD_NAME.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_ALLOWED_POD_NAME.getMsg());
                    }
                }
            } else {
                break;
            }
        }

        for (String temp : yamlArray) {
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String createYamlResourceNamespace;

            if (YamlMetadata.get(NAMESPACE_KEY) != null) {
                createYamlResourceNamespace = YamlMetadata.get(NAMESPACE_KEY).toString();

                if (namespace.equals(createYamlResourceNamespace)) {
                    break;
                } else {
                    LOGGER.info("the namespace of the provided object does not match the namespace sent on the request':::::::::error");
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.name(),
                            CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_MATCH_NAMESPACES.getMsg());
                }
            } else {
                break;
            }

        }

        for (String temp : yamlArray) {
            String kind = YamlUtil.parsingYaml(temp, KIND_KEY);

            String resourceKind = YamlUtil.makeResourceNameYAML(kind);

            if (resourceKind.equals(requestResource)) {
                isExistResource = true;
                break;
            }
        }

        if (!isExistResource) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_EXIST_RESOURCE.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(),
                    requestResource + MessageConstant.NOT_EXIST.getMsg());
        }

        for (String temp : yamlArray) {
            String resourceKind = YamlUtil.parsingYaml(temp, KIND_KEY);

            //isAdmin check
            if(!isAdmin){
                if(propertyService.getAdminResource().contains(resourceKind)) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.name(),
                            CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.INCLUDE_INACCESSIBLE_RESOURCES.getMsg());
                }
            }

            Object dryRunResult = InspectionUtil.resourceDryRunCheck("CreateUrl", HttpMethod.POST, namespace, resourceKind, temp, Constants.NULL_REPLACE_TEXT, params);
            ObjectMapper oMapper = new ObjectMapper();
            ResultStatus createdRs = oMapper.convertValue(dryRunResult, ResultStatus.class);

            if (Constants.RESULT_STATUS_FAIL.equals(createdRs.getResultCode())) {
                LOGGER.info("DryRun :: Not valid yaml ");
                return createdRs;
            }

        }

        return joinPoint.proceed(parameterValues);
    }

    /**
     * API URL 호출 시 update 메소드인 경우 메소드 수행 전 처리 (do preprocessing, if update method is)
     *
     * @param joinPoint the joinPoint
     * @throws Throwable
     */
    @Around("execution(* org.paasta.container.platform.api..*Controller.*update*(..))" +
            "&& !execution(* org.paasta.container.platform.api.clusters.cloudAccounts.*.*(..))" +
            "&& !execution(* org.paasta.container.platform.api.clusters.clusters.*.*(..))" +
            "&& !execution(* org.paasta.container.platform.api.clusters.hclTemplates.*.*(..))")
    public Object updateResourceAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        String yaml = null;
        String namespace = null;
        String resourceName = null;

        Object[] parameterValues = Arrays.asList(joinPoint.getArgs()).toArray();

        Params params = (Params) parameterValues[0];
        yaml = params.getYaml();
        namespace = params.getNamespace();
        resourceName = params.getResourceName();
        String resource;

        String requestResource;
        String requestURI = request.getRequestURI();

        if (StringUtils.isEmpty(resourceName)) {
            requestResource = InspectionUtil.parsingRequestURI(requestURI)[3];
            resourceName = namespace;
        } else {
            requestResource = InspectionUtil.parsingRequestURI(requestURI)[5];
        }
        requestResource = InspectionUtil.makeResourceName(requestResource);

        resource = InspectionUtil.parsingRequestURI(requestURI)[5];
        params.setResource(resource);


        if (StringUtils.isEmpty(yaml)) {
            YamlUtil.makeResourceYaml(params);
        }

        yaml = params.getYaml();


        //requestResource = InspectionUtil.makeResourceName(requestResource);

        String resourceKind = YamlUtil.parsingYaml(yaml, KIND_KEY);
        resourceKind = YamlUtil.makeResourceNameYAML(resourceKind);

        String[] yamlArray = YamlUtil.splitYaml(yaml);
        for (String temp : yamlArray) {
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String updateYamlResourceName = YamlMetadata.get(METADATA_NAME_KEY).toString();
            String updateYamlResourceNamespace;

            if (YamlMetadata.get(NAMESPACE_KEY) != null) {
                updateYamlResourceNamespace = YamlMetadata.get(NAMESPACE_KEY).toString();
            } else {
                updateYamlResourceNamespace = null;
            }

            if (StringUtils.isNotEmpty(updateYamlResourceName) && StringUtils.isNotEmpty(updateYamlResourceNamespace)) {
                if (updateYamlResourceName.startsWith("kube")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                } else {
                    break;
                }
            } else if (StringUtils.isNotEmpty(updateYamlResourceName) && StringUtils.isEmpty(updateYamlResourceNamespace)) {
                if (updateYamlResourceName.startsWith("kube")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                } else {
                    break;
                }
            }
        }

        for (String temp : yamlArray) {
            String YamlKind = YamlUtil.parsingYaml(temp, KIND_KEY);
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);

            String createYamlResourceName = YamlMetadata.get(METADATA_NAME_KEY).toString();

            if (YamlKind.equals(Constants.RESOURCE_POD)) {
                for (String na : NOT_ALLOWED_POD_NAME_LIST) {
                    if (createYamlResourceName.equals(na)) {
                        return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_POD_NAME.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_ALLOWED_POD_NAME.getMsg());
                    }
                }
            } else {
                break;
            }
        }

        for (String temp : yamlArray) {
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String createYamlResourceNamespace;

            if (YamlMetadata.get(NAMESPACE_KEY) != null) {
                createYamlResourceNamespace = YamlMetadata.get(NAMESPACE_KEY).toString();

                if (namespace.equals(createYamlResourceNamespace)) {
                    break;
                } else {
                    LOGGER.info("the namespace of the provided object does not match the namespace sent on the request':::::::::error");
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.name(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_MATCH_NAMESPACES.getMsg());
                }
            } else {
                break;
            }

        }

        String updateYamlResourceName = YamlUtil.parsingYaml(yaml, METADATA_KEY);

        if (!requestResource.equals(resourceKind) ) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_EXIST_RESOURCE.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), requestResource + MessageConstant.NOT_EXIST.getMsg());
        }

        if (!resourceName.equals(updateYamlResourceName)) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL,
                    MessageConstant.NOT_ALLOWED_RESOURCE_NAME.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_UPDATE_YAML_FORMAT_THIS_RESOURCE.getMsg());
        }

        resourceKind = YamlUtil.parsingYaml(yaml, KIND_KEY);

        if (StringUtils.isNotEmpty(resourceKind) && StringUtils.isNotEmpty(yaml)) {
            Object dryRunResult = InspectionUtil.resourceDryRunCheck("UpdateUrl", HttpMethod.PUT, namespace, resourceKind, yaml, resourceName, params);
            ObjectMapper oMapper = new ObjectMapper();
            ResultStatus updatedRs = oMapper.convertValue(dryRunResult, ResultStatus.class);
            if (Constants.RESULT_STATUS_FAIL.equals(updatedRs.getResultCode())) {
                LOGGER.info("DryRun :: Not valid yaml ");
                return updatedRs;
            }
        }

        return joinPoint.proceed(joinPoint.getArgs());
    }
}
