package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import org.paasta.container.platform.api.common.model.CommonOwnerReferences;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.storages.persistentVolumes.support.ObjectReference;
import org.paasta.container.platform.api.users.Users;

/**
 * Common Utils 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
public class CommonUtils {

    /**
     * Timestamp Timezone 을 변경하여 재설정(reset timestamp)
     *
     * @param requestTimestamp the request timestamp
     * @return the string
     */
    public static String procSetTimestamp(String requestTimestamp) {
        String resultString = "";

        if (null == requestTimestamp || "".equals(requestTimestamp)) {
            return resultString;
        }

        SimpleDateFormat simpleDateFormatForOrigin = new SimpleDateFormat(Constants.STRING_ORIGINAL_DATE_TYPE);
        SimpleDateFormat simpleDateFormatForSet = new SimpleDateFormat(Constants.STRING_DATE_TYPE);

        try {
            Date parseDate = simpleDateFormatForOrigin.parse(requestTimestamp);
            long parseDateTime = parseDate.getTime();
            int offset = TimeZone.getTimeZone(Constants.STRING_TIME_ZONE_ID).getOffset(parseDateTime);

            resultString = simpleDateFormatForSet.format(parseDateTime + offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultString;
    }

    /**
     * 넘어온 Object 의 parameter 가 null 또는 ""(빈 값)인지 체크 (check that the parameter is null or empty)
     *
     * @param obj the obj
     * @return the Object
     */
    public static Object stringNullCheck(Object obj) {
        List<String> checkParamList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.convertValue(obj, Map.class);

        for (String key : map.keySet()) {
            if (!StringUtils.hasText(map.get(key))) {
                checkParamList.add(key);
            }
        }

        if (checkParamList.size() > 0) {
            return ResultStatus.builder()
                .resultCode(Constants.RESULT_STATUS_FAIL)
                .resultMessage(MessageConstant.REGISTER_FAIL.getMsg())
                .httpStatusCode(400)
                .detailMessage(MessageConstant.REGISTER_FAIL.getMsg() + " " + checkParamList.toString() + " 항목을 다시 확인해주세요.").build();
        }

        return objectMapper.convertValue(map, Users.class);
    }

    /**
     * Yaml match map
     *
     * @param username  the username
     * @param namespace the namespace
     * @return the map
     */
    public static Map<String, Object> yamlMatch(String username, String namespace) {
        return new HashMap<String, Object>() {{
            put("userName", username);
            put("spaceName", namespace);
        }};
    }

    /**
     * 정규 표현식에 일치하는 지 체크(check regex)
     *
     * @param users the users
     * @return the String
     */
    public static String regexMatch(Users users) {
        String defaultValue = Constants.RESULT_STATUS_SUCCESS;

        boolean userIdCheck = Pattern.matches("^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*", users.getUserId());
        boolean passwordCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPassword());
        boolean passwordConfirmCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPasswordConfirm());
        boolean emailCheck = Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", users.getEmail());

        if (!userIdCheck) {
            return "User ID는 최대 253자 내의 영문 소문자 또는 숫자로 시작하고 끝나야 하며, 특수문자는 - 또는 . 만 사용 가능합니다.";
        } else if (!passwordCheck) {
            return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
        }
        else if (!passwordConfirmCheck) {
            return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
        }
        else if (!users.getPassword().equals(users.getPasswordConfirm())) {
            return "비밀번호가 서로 일치하지 않습니다.";
        }
        else if (!emailCheck) {
            return "이메일 형식이 잘못되었습니다.";
        }

        return defaultValue;
    }



    /**
     * 운영자 용 정규 표현식에 일치하는 지 체크 (check regex by admin)
     *
     * @param users the users
     * @return the String
     */
    public static String regexMatchByAdmin(Users users) {
        String defaultValue = Constants.RESULT_STATUS_SUCCESS;

        boolean userIdCheck = Pattern.matches("^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*", users.getUserId());
        boolean emailCheck = Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", users.getEmail());


        if (!userIdCheck) {
            return "User ID는 최대 253자 내의 영문 소문자 또는 숫자로 시작하고 끝나야 하며, 특수문자는 - 또는 . 만 사용 가능합니다.";
        }  else if (!emailCheck) {
            return "이메일 형식이 잘못되었습니다.";
        }


        if(!users.getPassword().equals(Constants.NULL_REPLACE_TEXT) || !users.getPasswordConfirm().equals(Constants.NULL_REPLACE_TEXT)) {

            boolean passwordCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPassword());
            boolean passwordConfirmCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPasswordConfirm());

            if (!passwordCheck) {
                return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
            }
            else if (!passwordConfirmCheck) {
                return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
            }
            else if (!users.getPassword().equals(users.getPasswordConfirm())) {
                return "비밀번호가 서로 일치하지 않습니다.";
            }
        }


        return defaultValue;
    }





    /**
     * 운영자 회원가입 용 정규 표현식에 일치하는 지 체크(check regex)
     *
     * @param users the users
     * @return the String
     */
    public static String regexMatchAdminSignUp(Users users) {
        String defaultValue = Constants.RESULT_STATUS_SUCCESS;

        boolean userIdCheck = Pattern.matches("^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*", users.getUserId());
        boolean passwordCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPassword());
        boolean passwordConfirmCheck = Pattern.matches("^[a-zA-Z]+(?=.*\\d)(?=.*[-$@$!%*#?&])[a-zA-Z\\d-$@$!%*#?&]{3,39}$", users.getPasswordConfirm());
        boolean emailCheck = Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", users.getEmail());

        if (!userIdCheck) {
            return "User ID는 최대 253자 내의 영문 소문자 또는 숫자로 시작하고 끝나야 하며, 특수문자는 - 또는 . 만 사용 가능합니다.";
        }
        else if (!passwordCheck) {
            return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
        }
        else if (!passwordConfirmCheck) {
            return "비밀번호는 영문으로 시작하고, 최소 하나 이상의 숫자와 특수 문자를 혼합하여 4~40자 이내로 사용 가능합니다.";
        }
        else if (!users.getPassword().equals(users.getPasswordConfirm())) {
            return "비밀번호가 서로 일치하지 않습니다.";
        }
        else if (!emailCheck) {
            return "이메일 형식이 잘못되었습니다.";
        }
        else if(users.getClusterName().equals(Constants.NULL_REPLACE_TEXT)) {
            return "Kubernetes Cluster 명을 입력해주세요.";
        }
        else if(users.getClusterApiUrl().equals(Constants.NULL_REPLACE_TEXT)) {
            return "Kubernetes Cluster API URL 을 입력해주세요.";
        }
        else if(users.getClusterToken().equals(Constants.NULL_REPLACE_TEXT)) {
            return "Kubernetes Cluster Token 을 입력해주세요.";
        }
        else if(users.getCpNamespace().equals(Constants.NULL_REPLACE_TEXT)) {
            return "Namespace 명을 입력해주세요.";
        }

        return defaultValue;
    }




    /**
     * Object 목록 값 수정(Object List value modify)
     *
     * @param obj    the obj
     * @param index  the index
     * @param newObj the newObj
     * @return the Object List
     */
    public static Object[] modifyValue(Object[] obj, int index, Object newObj) {
        ArrayList<Object> list = new ArrayList<>(Arrays.asList(obj));
        if (index < list.size()) {
            list.set(index, newObj);
        }

        return list.toArray();
    }

    /**
     * Resource name check string
     *
     * @param resourceName the resource name
     * @return the string
     */
    public static String resourceNameCheck(String resourceName) {
        return (resourceName == null) ? Constants.noName : resourceName;
    }

    /**
     * Is result status instance check boolean
     *
     * @param object the object
     * @return the boolean
     */
    public static boolean isResultStatusInstanceCheck(Object object) {
        return object instanceof ResultStatus;
    }

    /**
     * Proc replace null value object
     *
     * @param requestObject the request object
     * @return object
     */
    public static Object procReplaceNullValue(Object requestObject) {
        return (StringUtils.isEmpty(requestObject)) ? Constants.NULL_REPLACE_TEXT : requestObject;
    }

    /**
     * Proc replace null value string
     *
     * @param requestString the request string
     * @return the string
     */
    public static String procReplaceNullValue(String requestString) {
        return (StringUtils.isEmpty(requestString)) ? Constants.NULL_REPLACE_TEXT : requestString;
    }

    /**
     * Proc replace null value object reference
     *
     * @param requestObjectReference the request object reference
     * @return the object reference
     */
    public static ObjectReference procReplaceNullValue(ObjectReference requestObjectReference) {
        return (StringUtils.isEmpty(requestObjectReference)) ? new ObjectReference() {
            {
                setName(Constants.NULL_REPLACE_TEXT);
                setNamespace(Constants.NULL_REPLACE_TEXT);
            }
        } : requestObjectReference;
    }

    /**
     * Proc replace null value map
     *
     * @param requestMap the request map
     * @return the map
     */
    public static Map<String, Object> procReplaceNullValue(Map<String, Object> requestMap) {
        return (StringUtils.isEmpty(requestMap)) ? new HashMap<String, Object>() {{
            put(Constants.SUPPORTED_RESOURCE_STORAGE, Constants.NULL_REPLACE_TEXT);
        }} : requestMap;
    }

    /**
     * Proc replace null value list
     *
     * @param requestList           the request list
     * @param requestListObjectType the request list object type
     * @return the list
     */
    public static List<?> procReplaceNullValue(List<?> requestList, Constants.ListObjectType requestListObjectType) {
        List<?> resultList;

        switch (requestListObjectType) {
            case LIMIT_RANGES_ITEM:
                resultList = (StringUtils.isEmpty(requestList)) ? new ArrayList<String>() {{
                    add(Constants.NULL_REPLACE_TEXT);
                }} : requestList;
                break;

            case COMMON_OWNER_REFERENCES:
                resultList = (StringUtils.isEmpty(requestList)) ? new ArrayList<CommonOwnerReferences>() {
                    {
                        add(new CommonOwnerReferences() {{
                            setName(Constants.NULL_REPLACE_TEXT);
                        }});
                    }
                } : requestList;
                break;

            default:
                resultList = (StringUtils.isEmpty(requestList)) ? new ArrayList<String>() {{
                    add(Constants.NULL_REPLACE_TEXT);
                }} : requestList;
        }
        return resultList;
    }


    /**
     * LOGGER 개행문자 제거 (Object)
     *
     * @param obj
     * @return String the replaced string
     */
    public static String loggerReplace(Object obj) {
        return obj.toString().replaceAll("[\r\n]","");
    }

    /**
     * LOGGER 개행문자 제거 (String)
     *
     * @param str
     * @return String the replaced string
     */
    public static String loggerReplace(String str) {
        return str.replaceAll("[\r\n]","");
    }

}
