package org.container.platform.api.common.util;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonStatusCode;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.exception.ContainerPlatformException;
import org.container.platform.api.exception.ResultStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Yaml Util 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.21
 **/
@Component
public class YamlUtil {
    private static PropertyService propertyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(YamlUtil.class);

    @Autowired
    public YamlUtil(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    /**
     * YAML 의 Resource 값 조회(Get YAML's resource)
     *
     * @param yaml    the yaml
     * @param keyword the keyword
     * @return the string
     */
    public static String parsingYaml(String yaml, String keyword) {
        String value = null;
        try {
            Yaml y = new Yaml();
            Map<String, Object> yamlMap = y.load(yaml);

            if ("kind".equals(keyword)) {
                value = (String) yamlMap.get(keyword);
            } else if ("metadata".equals(keyword)) {
                Map a = (Map) yamlMap.get(keyword);
                value = a.get("name").toString();
            }

        } catch (ClassCastException e) {
            throw new ContainerPlatformException(Constants.RESULT_STATUS_FAIL, MessageConstant.INVALID_YAML_FORMAT.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.INVALID_YAML_FORMAT.getMsg());
        }

        return value;
    }


    /**
     * YAML Resource 값 조회(Get YAML resource value)
     *
     * @param yaml    the yaml
     * @param keyword the keyword
     * @return the map
     */
    public static Map parsingYamlMap(String yaml, String keyword) {
        Map value = null;
        try {
            Yaml y = new Yaml();
            Map<String, Object> yamlMap = y.load(yaml);

            if ("metadata".equals(keyword)) {
                value = (Map) yamlMap.get(keyword);
            }

        } catch (ClassCastException e) {
            throw new ContainerPlatformException(Constants.RESULT_STATUS_FAIL, MessageConstant.INVALID_YAML_FORMAT.getMsg(), CommonStatusCode.UNPROCESSABLE_ENTITY.getCode(), MessageConstant.INVALID_YAML_FORMAT.getMsg());
        }

        return value;
    }


    /**
     * URL Resource 값과 비교할 YAML Resource 값 조회(Get YAML's resource to compare URL Resource)
     *
     * @param kind the kind
     * @return the string
     */
    public static String makeResourceNameYAML(String kind) {
        String YamlKind = kind.toLowerCase();

        return YamlKind;
    }


    /**
     * 복합 yaml List 로 조회(Get list of multiple YAML)
     *
     * @param yaml the yaml
     * @return the string[]
     */
    public static String[] splitYaml(String yaml) {
        String[] yamlArray = yaml.split("---");
        ArrayList<String> returnList = new ArrayList<String>();

        for (String temp : yamlArray) {
            temp = temp.trim();
            if (temp.length() > 0) {
                returnList.add(temp);
            }
        }
        return returnList.toArray(new String[returnList.size()]);
    }

    /**
     * resources yaml 조합(Assemble resource YAML)
     *
     * @param params the params
     */
    public static void makeResourceYaml(Params params) {
        String yaml = "";

        try {
            //ingress
            if (InspectionUtil.makeResourceName(params.getResource()).equals(Constants.RESOURCE_INGRESS.toLowerCase())) {

                if (params.getMetadataName().equals(Constants.EMPTY_STRING)) {
                    throw new ResultStatusException(MessageConstant.REQUEST_VALUE_IS_MISSING.getMsg());
                }

                String topYaml = "apiVersion: networking.k8s.io/v1\nkind: Ingress\nmetadata:\n  name: " + params.getMetadataName() + "\n  namespace: " + params.getNamespace() + "\nspec:\n  rules:";

                for (int i = 0; i < params.rules.size(); i++) {

                    int key = params.rules.get(i).toString().indexOf(":");
                    String host = "";
                    String pathType = "";
                    String path = "";
                    String target = "";
                    String port = "";

                    if (params.rules.get(i).toString().contains("host")) {
                        host = params.rules.get(i).toString().substring(key + 1);
                        String rulesYaml = "\n  - host: " + host + "\n    http:\n      paths:";
                        topYaml += rulesYaml;
                    } else if (params.rules.get(i).toString().contains("pathType")) {
                        pathType = params.rules.get(i).toString().substring(key + 1);
                        String pathTypeYaml = "\n      - pathType: " + pathType;
                        topYaml += pathTypeYaml;
                    } else if (params.rules.get(i).toString().contains("path")) {
                        path = params.rules.get(i).toString().substring(key + 1);
                        String pathYaml = "\n        path: " + path;
                        topYaml += pathYaml;
                    } else if (params.rules.get(i).toString().contains("target")) {
                        target = params.rules.get(i).toString().substring(key + 1);
                        String targetYaml = "\n        backend:\n          service:\n            name: " + target;
                        topYaml += targetYaml;
                    } else if (params.rules.get(i).toString().contains("port")) {
                        port = params.rules.get(i).toString().substring(key + 1);
                        String portYaml = "\n            port:\n              number: " + port;
                        topYaml += portYaml;
                    }
                }
                yaml = topYaml;
                //secret
            } else if (InspectionUtil.makeResourceName(params.getResource()).equals(Constants.RESOURCE_SECRET.toLowerCase())) {
                String topYaml = "apiVersion: v1\nkind: Secret\nmetadata:\n  name: " + params.getMetadataName() + "\n  namespace: " + params.getNamespace() + "\ntype: " + params.getDataType();
                yaml = topYaml;
            }
            if (params.getYaml().isEmpty()) {
                params.setYaml(yaml);
            }
        } catch (ResultStatusException e) {
            throw new ResultStatusException(e.getErrorMessage());
        } catch (Exception e) {
            throw new ResultStatusException(MessageConstant.RESOURCE_CREATION_FAILED.getMsg());
        }
    }


    /**
     * Pod 내 트래픽 허용 Labels 추가 (Add Allow Traffic in Pod Labels)
     *
     * @param params the params
     * @param isPod  the isPod
     */
    public static void allowTrafficPodLabels(Params params, Boolean isPod) {
        if (params.getAllowTraffic()) {
            String path = isPod ? "metadata" : "spec.template.metadata";
            String[] labels = propertyService.getCpTrafficPolicyAllowedLabels().split("=");
            params.setYaml(addLabels(params.getYaml(), path, labels));
        }
    }

    /**
     * Labels 추가 (Add Labels)
     *
     * @param yamlString the yamlString
     * @param path       the path
     * @param labels     the labels
     */
    public static String addLabels(String yamlString, String path, String[] labels) {
        String updateYamlString = "";
        try {
            // string -> map
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(yamlString);
            Map targetMap = (Map) getNestedValue(yamlMap, path);
            ((Map) targetMap.computeIfAbsent("labels", k -> new HashMap<>())).put(labels[0], labels[1]);

            // map -> string
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yamlDumper = new Yaml(options);
            updateYamlString = yamlDumper.dump(yamlMap);

        } catch (Exception e) {
            LOGGER.info("exception occurred while add pod labels >> " + e.getMessage());
            return yamlString;
        }

        return updateYamlString;
    }


    /**
     * path에 따른 Map 반환 (Return map along path)
     *
     * @param map  the map
     * @param path the path
     */
    public static Object getNestedValue(Map map, String path) {
        String[] keys = path.split("\\.");
        Object value = map;
        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map) value).get(key);
            } else {
                return null;
            }
        }
        return value;
    }

}
