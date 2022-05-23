package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.paasta.container.platform.api.adminToken.AdminToken;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.CpCommonAPIException;
import org.paasta.container.platform.api.login.JwtUtil;
import org.paasta.container.platform.api.users.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;

/**
 * Rest Template Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
public class RestTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String commonApiBase64Authorization;
    private final RestTemplate restTemplate;
    private final PropertyService propertyService;
    private String base64Authorization;
    private String baseUrl;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Instantiates a new Rest template service
     * @param restTemplate                   the rest template
     * @param commonApiAuthorizationId       the common api authorization id
     * @param commonApiAuthorizationPassword the common api authorization password
     * @param propertyService                the property service
     */
    @Autowired
    public RestTemplateService(RestTemplate restTemplate,
                               @Value("${commonApi.authorization.id}") String commonApiAuthorizationId,
                               @Value("${commonApi.authorization.password}") String commonApiAuthorizationPassword,
                               PropertyService propertyService) {
        this.restTemplate = restTemplate;
        this.propertyService = propertyService;

        this.commonApiBase64Authorization = "Basic "
                + Base64Utils.encodeToString(
                (commonApiAuthorizationId + ":" + commonApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * t 전송(Send t)
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * t 전송(Send t)
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * t 전송(Send t)
     *
     * (Admin)
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @return the t
     */
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE);
    }



    /**
     * t 전송(Send t)
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType) {

        setApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));
        ResponseEntity<T> resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);

        if (resEntity.getBody() != null) {
            LOGGER.info("RESPONSE-TYPE: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
        } else {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }

    /**
     * t 전송(Send t)
     *
     * (Admin)
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * t 전송(Send t)
     *
     * (Admin)
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType) {

        setApiUrlAuthorizationAdmin(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getRawStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));

            for (CommonStatusCode code : CommonStatusCode.class.getEnumConstants()) {
                if(code.getCode() == exception.getRawStatusCode()) {
                    return (T) new ResultStatus(Constants.RESULT_STATUS_FAIL, exception.getStatusText(), code.getCode(), code.getMsg());
                }
            }
        }

        if (resEntity.getBody() != null) {
            LOGGER.info("RESPONSE-TYPE: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
            return statusCodeDiscriminate(reqApi, resEntity, httpMethod);

        } else {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }

    /**
     * Authorization 값을 입력(Set the authorization value)
     *
     * @param reqApi the reqApi
     */
    private void setApiUrlAuthorization(String reqApi) {

        String apiUrl = "";
        String authorization = "";
        String namespace = "";
        String saUserToken = "";
        String userName = "";
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestUri = request.getRequestURI();

        // CONTAINER PLATFORM MASTER API
        if (Constants.TARGET_CP_MASTER_API.equals(reqApi)) {
            namespace = getNs(requestUri);
            saUserToken = jwtUtil.extractJwtFromRequest(request);
            userName = jwtUtil.getUsernameFromToken(saUserToken);
            apiUrl = propertyService.getCpMasterApiUrl();
            if(namespace.equals(Constants.NULL_REPLACE_TEXT))
                authorization = "Bearer " + this.getAdminToken().getTokenValue();
            else
                authorization = "Bearer " + this.getUserInfo(userName, namespace).getSaToken();
        }

        // COMMON API
        if (TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        this.base64Authorization = authorization;
        this.baseUrl = apiUrl;
    }

    /**
     * Authorization 값을 입력(Set the authorization value)
     *
     * @param reqApi the reqApi
     */
    private void setApiUrlAuthorizationAdmin(String reqApi) {

        String apiUrl = "";
        String authorization = "";

        // CONTAINER PLATFORM MASTER API
        if (Constants.TARGET_CP_MASTER_API.equals(reqApi)) {
            apiUrl = propertyService.getCpMasterApiUrl();
            authorization = "Bearer " + this.getAdminToken().getTokenValue();
        }

        // COMMON API
        if (TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        this.base64Authorization = authorization;
        this.baseUrl = apiUrl;
    }

    /**
     * requestURI 에서 namespace 명 추출(Extract namespace name from requestURI)
     *
     * @param URI the requestURI
     * @return the String
     */
    public String getNs(String URI) {
        String namespace = Constants.NULL_REPLACE_TEXT;
        int nsOrder = 0;
        try {
            if (URI.indexOf("namespaces") > 0) {
                String[] arrString = URI.split("/");
                for (int i = 0; i < arrString.length; i++) {
                    if (arrString[i].equals("namespaces")) {
                        nsOrder = i + 1;
                        if (Constants.CLUSTER_ROLE_URI.indexOf(arrString[nsOrder + 1])>=0)
                            return namespace;
                    }
                }
                namespace = arrString[nsOrder];
            }
        }catch(Exception e){
            return namespace;
        }
        return namespace;
    }

    /**
     * Admin Token 상세 정보를 조회(Get the Admin Token Detail)
     *
     * @return the AdminToken
     */
    public AdminToken getAdminToken() {
        this.setApiUrlAuthorization(TARGET_COMMON_API);
        String reqUrl = Constants.URI_COMMON_API_ADMIN_TOKEN_DETAIL.replace("{tokenName:.+}",Constants.TOKEN_KEY);
        AdminToken adminToken = this.send(TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, AdminToken.class);

        if(Constants.RESULT_STATUS_FAIL.equals(adminToken.getResultCode())) {
            throw new CpCommonAPIException(adminToken.getResultCode(), CommonStatusCode.NOT_FOUND.getMsg(), adminToken.getStatusCode(), CommonStatusCode.NOT_FOUND.getMsg());
        }

        return adminToken;
    }

    public Users getUserInfo(String username, String namespace) {
        this.setApiUrlAuthorization(TARGET_COMMON_API);
        String reqUrl = Constants.URI_COMMON_API_USERS.replace("{cluster:.+}", "cp-namespace").replace("{namespace:.+}", namespace).replace("{userId:.+}", username);
        Users users = this.send(TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, Users.class);

        if(Constants.RESULT_STATUS_FAIL.equals(users.getResultCode())) {
            throw new CpCommonAPIException(users.getResultCode(), CommonStatusCode.NOT_FOUND.getMsg(), 0, CommonStatusCode.NOT_FOUND.getMsg());
        }

        return users;
    }


    /**
     * 사용자가 보낸 YAML 그대로 REST API Call 하는 메소드(Call the Rest Api)
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param isAdmin the is Admin
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Boolean isAdmin) {
        if(isAdmin)
            return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");
        else
            return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");
    }


    /**
     * 생성, 갱신, 삭제 로직의 코드 식별(Create/Update/Delete logic's status code discriminate)
     *
     * @param reqApi      the reqApi
     * @param res         the response
     * @param httpMethod  the http method
     * @return            the t
     */
    public <T> T statusCodeDiscriminate(String reqApi, ResponseEntity<T> res, HttpMethod httpMethod) {
        // 200, 201, 202일때 결과 코드 동일하게(Same Result Code = 200, 201, 202)
        Integer[] RESULT_STATUS_SUCCESS_CODE = {200, 201, 202};

        ResultStatus resultStatus;

        List<Integer> intList = new ArrayList<>(RESULT_STATUS_SUCCESS_CODE.length);
        for (int i : RESULT_STATUS_SUCCESS_CODE)
        {
            intList.add(i);
        }

        // Rest 호출 시 에러가 났지만 에러 메세지를 보여주기 위해 200 OK로 리턴된 경우 (Common API Error Object)
        if(Constants.TARGET_COMMON_API.equals(reqApi)) {
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(res.getBody(), Map.class);

            if(Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                resultStatus = new ResultStatus(Constants.RESULT_STATUS_FAIL, map.get("resultMessage").toString(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
                return (T) resultStatus;
            }
        }


        if (httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE) {
            if (Arrays.asList(RESULT_STATUS_SUCCESS_CODE).contains(res.getStatusCode().value()) ) {
                resultStatus = new ResultStatus(Constants.RESULT_STATUS_SUCCESS, res.getStatusCode().toString(), CommonStatusCode.OK.getCode(), CommonStatusCode.OK.getMsg());
                return (T) resultStatus;
            }
        }

        return res.getBody();
    }


    /**
     * service account 의 secret 이름을 조회(Get Secret of Service Account)
     *
     * @param namespace the namespace
     * @param userName the user name
     * @return the String
     */
    public String getSecretName(String namespace, String userName) {
        String jsonObj = this.sendAdmin(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersGetUrl().replace("{namespace}", namespace).replace("{name}", userName), HttpMethod.GET, null, String.class);

        JsonObject jsonObject = JsonParser.parseString(jsonObj).getAsJsonObject();
        JsonElement element = jsonObject.getAsJsonObject().get("secrets");
        element = element.getAsJsonArray().get(0);
        String token = element.getAsJsonObject().get("name").toString();
        token = token.replaceAll("\"", "");
        return token;
    }
}
