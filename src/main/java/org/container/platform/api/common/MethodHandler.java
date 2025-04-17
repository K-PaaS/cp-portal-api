package org.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.container.platform.api.common.model.CommonStatusCode;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.common.util.InspectionUtil;
import org.container.platform.api.common.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Map;

import static org.container.platform.api.common.Constants.NOT_ALLOWED_POD_NAME_LIST;

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
    @Around("execution(* org.container.platform.api..*Controller.*create*(..))" +
            "&& !execution(* org.container.platform.api.clusters.cloudAccounts.*.*(..))" +
            "&& !execution(* org.container.platform.api.clusters.clusters.*.*(..))" +
            "&& !execution(* org.container.platform.api.clusters.hclTemplates.*.*(..))")
    public Object createResourceAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        String yaml = "";
        String namespace = "";
        String resource;
        String requestResource;
        String requestURI = request.getRequestURI();
        Boolean isExistResource = false;

        Object[] parameterValues = Arrays.asList(joinPoint.getArgs()).toArray();
        Params params = (Params) parameterValues[0];
        namespace = params.getNamespace();

        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NAMESPACES_CANNOT_BE_CREATED.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NAMESPACES_CANNOT_BE_CREATED.getMsg());
        }

        requestResource = StringUtils.isEmpty(namespace) ? InspectionUtil.parsingRequestURI(requestURI)[3] : InspectionUtil.parsingRequestURI(requestURI)[5];
        resource = InspectionUtil.parsingRequestURI(requestURI)[5];
        params.setResource(resource);

        // ingress, secret
        if (StringUtils.isEmpty(yaml)) {
            YamlUtil.makeResourceYaml(params);
        }


        yaml = params.getYaml();
        requestResource = InspectionUtil.makeResourceName(requestResource);
        LOGGER.info("CREATING REQUEST RESOURCE :: " + CommonUtils.loggerReplace(requestResource));

        String[] yamlArray = YamlUtil.splitYaml(yaml);


        for (String temp : yamlArray) {
            String yamlKind = YamlUtil.parsingYaml(temp, KIND_KEY);
            Map YamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String createYamlResourceName = YamlMetadata.get(METADATA_NAME_KEY).toString();
            String createYamlResourceNamespace = YamlMetadata.get(NAMESPACE_KEY) != null ? YamlMetadata.get(NAMESPACE_KEY).toString() : null;
            if (!isExistResource) {
                isExistResource = yamlKind.equalsIgnoreCase(requestResource) ? true : false;
            }

            // check that the current and requested namespace match
            if (createYamlResourceNamespace != null && !namespace.equals(createYamlResourceNamespace)) {
                return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.name(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_MATCH_NAMESPACES.getMsg());
            }

            // check the prefix 'kube-' to the resource name
            if (StringUtils.isNotEmpty(createYamlResourceName)) {
                if (createYamlResourceName.startsWith("kube-")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                }
            }

            // check resource name that cannot be allowed if resource is pod
            if (yamlKind.equals(Constants.RESOURCE_POD)) {
                if (NOT_ALLOWED_POD_NAME_LIST.contains(createYamlResourceName)) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_POD_NAME.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_ALLOWED_POD_NAME.getMsg());
                }
            }

        }

        // check that yamls contain request resources
        if (!isExistResource) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_EXIST_RESOURCE.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), requestResource + MessageConstant.NOT_EXIST.getMsg());
        }

        // check dry-run for each resource
        for (String temp : yamlArray) {
            String resourceKind = YamlUtil.parsingYaml(temp, KIND_KEY);
            if (!requestResource.equals(Constants.RESOURCE_SECRET.toLowerCase())) {
                Object dryRunResult = InspectionUtil.resourceDryRunCheck("CreateUrl", HttpMethod.POST, namespace, resourceKind, temp, Constants.NULL_REPLACE_TEXT, params);
                ObjectMapper oMapper = new ObjectMapper();
                ResultStatus createdRs = oMapper.convertValue(dryRunResult, ResultStatus.class);

                if (Constants.RESULT_STATUS_FAIL.equals(createdRs.getResultCode())) {
                    LOGGER.info("[CREATE RESOURCE DRY-RUN] FAILED :: NOT VALID YAML");
                    return createdRs;
                }
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
    @Around("execution(* org.container.platform.api..*Controller.*update*(..))" +
            "&& !execution(* org.container.platform.api.clusters.cloudAccounts.*.*(..))" +
            "&& !execution(* org.container.platform.api.clusters.clusters.*.*(..))" +
            "&& !execution(* org.container.platform.api.clusters.hclTemplates.*.*(..))")
    public Object updateResourceAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        String yaml = "";
        String namespace = "";
        String resourceName = "";
        String resource;
        String requestResource;
        String requestURI = request.getRequestURI();


        Object[] parameterValues = Arrays.asList(joinPoint.getArgs()).toArray();
        Params params = (Params) parameterValues[0];
        yaml = params.getYaml();
        namespace = params.getNamespace();
        resourceName = params.getResourceName();


        requestResource = StringUtils.isEmpty(resourceName) ? InspectionUtil.parsingRequestURI(requestURI)[3] : InspectionUtil.parsingRequestURI(requestURI)[5];
        requestResource = InspectionUtil.makeResourceName(requestResource);
        resource = InspectionUtil.parsingRequestURI(requestURI)[5];
        params.setResource(resource);


        // ingress, secret
        if (StringUtils.isEmpty(yaml)) {
            YamlUtil.makeResourceYaml(params);
        }

        yaml = params.getYaml();
        String[] yamlArray = YamlUtil.splitYaml(yaml);


        for (String temp : yamlArray) {
            String yamlKind = YamlUtil.parsingYaml(temp, KIND_KEY);
            Map yamlMetadata = YamlUtil.parsingYamlMap(temp, METADATA_KEY);
            String updateYamlResourceName = yamlMetadata.get(METADATA_NAME_KEY).toString();
            String updateYamlResourceNamespace = yamlMetadata.get(NAMESPACE_KEY) != null ? yamlMetadata.get(NAMESPACE_KEY).toString() : null;

            // check that the current and requested resource match
            if (!requestResource.equalsIgnoreCase(yamlKind)) {
                return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_EXIST_RESOURCE.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), requestResource + MessageConstant.NOT_EXIST.getMsg());
            }

            // check that the current and requested resource name match
            if (!resourceName.equals(updateYamlResourceName)) {
                return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_RESOURCE_NAME.getMsg(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_UPDATE_YAML_FORMAT_THIS_RESOURCE.getMsg());
            }

            // check that the current and requested namespace match
            if (updateYamlResourceNamespace != null && !namespace.equals(updateYamlResourceNamespace)) {
                return new ResultStatus(Constants.RESULT_STATUS_FAIL, CommonStatusCode.BAD_REQUEST.name(), CommonStatusCode.BAD_REQUEST.getCode(), MessageConstant.NOT_MATCH_NAMESPACES.getMsg());
            }

            // check the prefix 'kube-' to the resource name
            if (StringUtils.isNotEmpty(updateYamlResourceName)) {
                if (updateYamlResourceName.startsWith("kube-")) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.PREFIX_KUBE_NOT_ALLOW.getMsg());
                }
            }

            // check resource name that cannot be allowed if resource is pod
            if (yamlKind.equals(Constants.RESOURCE_POD)) {
                if (NOT_ALLOWED_POD_NAME_LIST.contains(updateYamlResourceName)) {
                    return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.NOT_ALLOWED_POD_NAME.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.NOT_ALLOWED_POD_NAME.getMsg());
                }
            }

            // check dry-run resource
            if (!requestResource.equals(Constants.RESOURCE_SECRET.toLowerCase())) {
                Object dryRunResult = InspectionUtil.resourceDryRunCheck("UpdateUrl", HttpMethod.PUT, namespace, yamlKind, yaml, resourceName, params);
                ObjectMapper oMapper = new ObjectMapper();
                ResultStatus updatedRs = oMapper.convertValue(dryRunResult, ResultStatus.class);
                if (Constants.RESULT_STATUS_FAIL.equals(updatedRs.getResultCode())) {
                    LOGGER.info("[UPDATE RESOURCE DRY-RUN] FAILED :: NOT VALID YAML");
                    return updatedRs;
                }
            }

        }

        return joinPoint.proceed(joinPoint.getArgs());
    }


}