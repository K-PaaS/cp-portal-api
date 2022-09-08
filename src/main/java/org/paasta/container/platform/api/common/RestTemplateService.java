package org.paasta.container.platform.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.paasta.container.platform.api.adminToken.AdminToken;
import org.paasta.container.platform.api.clusters.clusters.Clusters;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.exception.CommonStatusCodeException;
import org.paasta.container.platform.api.exception.CpCommonAPIException;
import org.paasta.container.platform.api.users.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;
import static org.paasta.container.platform.api.common.Constants.TARGET_TERRAMAN_API;

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
    private final RestTemplate shortRestTemplate;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private final VaultService vaultService;
    private String base64Authorization;
    private String baseUrl;


    /**
     * Instantiates a new Rest template service
     *
     * @param restTemplate                   the rest template
     * @param commonApiAuthorizationId       the common api authorization id
     * @param commonApiAuthorizationPassword the common api authorization password
     * @param propertyService                the property service
     */
    @Autowired
    public RestTemplateService(RestTemplate restTemplate,
                               @Qualifier("shortTimeoutRestTemplate") RestTemplate shortRestTemplate,
                               @Value("${commonApi.authorization.id}") String commonApiAuthorizationId,
                               @Value("${commonApi.authorization.password}") String commonApiAuthorizationPassword,
                               PropertyService propertyService,
                               CommonService commonService,
                               VaultService vaultService) {
        this.restTemplate = restTemplate;
        this.shortRestTemplate = shortRestTemplate;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.vaultService = vaultService;
        this.commonApiBase64Authorization = "Basic "
                + Base64Utils.encodeToString(
                (commonApiAuthorizationId + ":" + commonApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
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
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE, params);
    }


    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }

    @TrackExecutionTime
    public <T> T sendGlobal(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Params params) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }

    public <T> T sendPing(String reqApi, Class<T> responseType, Params params) {
        return sendPing(reqApi, "", HttpMethod.GET, null, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }


    /**
     * 사용자가 보낸 YAML 그대로 REST API Call 하는 메소드(Call the Rest Api) isAdmin 제거
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, params.getYaml(), responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml", params);

    }


    /**
     * 리소스 생성 및 수정에 대한 DryRun 체크 메소드
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendDryRun(String reqApi, String reqUrl, HttpMethod httpMethod, String yaml, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl + "?dryRun=All", httpMethod,yaml, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml", params);

    }

    public String setRequestParameter(String reqApi, String reqUrl, HttpMethod httpMethod, Params params) {

        if (reqApi.equals(Constants.TARGET_CP_MASTER_API)) {
            if (httpMethod.equals(HttpMethod.GET) && params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
                reqUrl = reqUrl.replace("namespaces/{namespace}/", "");
             //   reqUrl += commonService.generateFieldSelectorForExceptNamespace(params.getSelectorType());
            }
            reqUrl = reqUrl.replace("{namespace}", params.getNamespace()).replace("{name}", params.getResourceName()).replace("{userId}", params.getUserId());
        }

        return reqUrl;
    }

    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
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
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params);
        setApiUrlAuthorizationClusterAdmin(reqApi, params);

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
            throw new CommonStatusCodeException(Integer.toString(exception.getRawStatusCode()));
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
     * t 전송(Send t)
     * <p></p>
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
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params);
        setApiUrlAuthorizationClusterAdmin(reqApi, params);

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
            throw new CommonStatusCodeException(Integer.toString(exception.getRawStatusCode()));
        }

        if (resEntity.getBody() == null) {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }


    /**
     * t 전송(Send t)
     * <p></p>
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
    public <T> T sendPing(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params); // TODO 중복 코드 제거 필요.
        setApiUrlAuthorizationClusterAdmin(reqApi, params);

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
            resEntity = shortRestTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getRawStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));
            throw new CommonStatusCodeException(Integer.toString(exception.getRawStatusCode()));
        }

        if (resEntity.getBody() == null) {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
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
            LOGGER.info("#####2#####");
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
            throw new CommonStatusCodeException(Integer.toString(exception.getRawStatusCode()));
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
            saUserToken = commonService.extractJwtFromRequest(request);
            userName = commonService.getUsernameFromToken(saUserToken);
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

        // TERRAMAN API
        if (TARGET_TERRAMAN_API.equals(reqApi)) {
            apiUrl = propertyService.getTerramanApiUrl();
            /*
            FIXME!!
            TERRAMAN API authorization
             */
            authorization = null;

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
                        if (Constants.CLUSTER_ROLE_URI.indexOf(arrString[nsOrder + 1]) >= 0)
                            return namespace;
                    }
                }
                namespace = arrString[nsOrder];
            }
        } catch (Exception e) {
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
        String reqUrl = Constants.URI_COMMON_API_ADMIN_TOKEN_DETAIL.replace("{tokenName:.+}", Constants.TOKEN_KEY);
        AdminToken adminToken = this.send(TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, AdminToken.class);

        if (Constants.RESULT_STATUS_FAIL.equals(adminToken.getResultCode())) {
            throw new CpCommonAPIException(adminToken.getResultCode(), CommonStatusCode.NOT_FOUND.getMsg(), adminToken.getStatusCode(), CommonStatusCode.NOT_FOUND.getMsg());
        }

        return adminToken;
    }

    public Users getUserInfo(String username, String namespace) {
        this.setApiUrlAuthorization(TARGET_COMMON_API);
        String reqUrl = Constants.URI_COMMON_API_USERS.replace("{cluster:.+}", "cp-namespace").replace("{namespace:.+}", namespace).replace("{userId:.+}", username);
        Users users = this.send(TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, Users.class);

        if (Constants.RESULT_STATUS_FAIL.equals(users.getResultCode())) {
            throw new CpCommonAPIException(users.getResultCode(), CommonStatusCode.NOT_FOUND.getMsg(), 0, CommonStatusCode.NOT_FOUND.getMsg());
        }

        return users;
    }


    /**
     * 생성, 갱신, 삭제 로직의 코드 식별(Create/Update/Delete logic's status code discriminate)
     *
     * @param reqApi     the reqApi
     * @param res        the response
     * @param httpMethod the http method
     * @return the t
     */
    public <T> T statusCodeDiscriminate(String reqApi, ResponseEntity<T> res, HttpMethod httpMethod) {
        // 200, 201, 202일때 결과 코드 동일하게(Same Result Code = 200, 201, 202)
        Integer[] RESULT_STATUS_SUCCESS_CODE = {200, 201, 202};
        ResultStatus resultStatus;

        List<Integer> intList = new ArrayList<>(RESULT_STATUS_SUCCESS_CODE.length);
        for (int i : RESULT_STATUS_SUCCESS_CODE) {
            intList.add(i);
        }

        // Rest 호출 시 에러가 났지만 에러 메세지를 보여주기 위해 200 OK로 리턴된 경우 (Common API Error Object)
        if (Constants.TARGET_COMMON_API.equals(reqApi)) {
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(res.getBody(), Map.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                resultStatus = new ResultStatus(Constants.RESULT_STATUS_FAIL, map.get("resultMessage").toString(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
                return (T) resultStatus;
            }
        }


        if (httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE) {
            if (Arrays.asList(RESULT_STATUS_SUCCESS_CODE).contains(res.getStatusCode().value())) {
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
     * @param userName  the user name
     * @return the String
     */
    public String getSecretName(String namespace, String userName) {
        String jsonObj = this.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListUsersGetUrl().replace("{namespace}", namespace).replace("{name}", userName), HttpMethod.GET, null, String.class);

        JsonObject jsonObject = JsonParser.parseString(jsonObj).getAsJsonObject();
        JsonElement element = jsonObject.getAsJsonObject().get("secrets");
        element = element.getAsJsonArray().get(0);
        String token = element.getAsJsonObject().get("name").toString();
        token = token.replaceAll("\"", "");
        return token;
    }



    /**수정 필요
     * Authorization 값을 입력(Set the authorization value)
     *
     * @param reqApi the reqApi
     */
    private void setApiUrlAuthorizationClusterAdmin(String reqApi, Params params) {
        String apiUrl = "";
        String authorization = "";

        // CONTAINER PLATFORM MASTER API
        if (Constants.TARGET_CP_MASTER_API.equals(reqApi)) {
            Clusters clusters = (params.getIsClusterToken()) ? vaultService.getClusterDetails(params.getCluster()) : commonService.getKubernetesInfo(params);
            Assert.notNull(clusters, "Invalid parameter");
            apiUrl = clusters.getClusterApiUrl();
            authorization = "Bearer " + clusters.getClusterToken();

        }
        // COMMON API
        if (TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        // TERRAMAN API
        if (TARGET_TERRAMAN_API.equals(reqApi)) {
            apiUrl = propertyService.getTerramanApiUrl();
        }

        this.base64Authorization = authorization;
        this.baseUrl = apiUrl;
    }


    /**
     * Clusters 정보 조회(Get Clusters Info)
     *
     * @param clusterId the cluster name
     * @return the clusters
     */
    public Clusters getClusters(String clusterId) {
        return send(Constants.TARGET_COMMON_API, "/clusters/" + clusterId, HttpMethod.GET, null, Clusters.class, new Params());
    }

    ///지울것
    ///////////////////////////////////////////////////////////////////////////

    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
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
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE);
    }

    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 사용자가 보낸 YAML 그대로 REST API Call 하는 메소드(Call the Rest Api)
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param isAdmin      the is Admin
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Boolean isAdmin) {
        if (isAdmin)
            return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");
        else
            return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");
    }


    ///////////////////////////////

    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
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
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, acceptType, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
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
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * 사용자가 보낸 YAML 그대로 REST API Call 하는 메소드(Call the Rest Api) isAdmin 제거
     *
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml");

    }

}