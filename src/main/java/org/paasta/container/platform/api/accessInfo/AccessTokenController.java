package org.paasta.container.platform.api.accessInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Access Token Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.29
 */
@Api(value = "AccessTokenController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/secrets")
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    /**
     * Instantiates a new AccessToken controller
     *
     * @param accessTokenService the accessToken service
     */
    @Autowired
    public AccessTokenController(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    /**
     * Secrets 상세 조회(Get Secrets detail)
     *
     * @param namespace the namespace
     * @param accessTokenName the accessTokenName
     * @return the AccessToken detail
     */
    @ApiOperation(value = "Secrets 상세 조회(Get Secrets detail)", nickname = "getSecret")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "accessTokenName", value = "액세스 토큰 명",  required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{accessTokenName:.+}")
    public AccessToken getSecret(@PathVariable("namespace") String namespace,
                                 @PathVariable("accessTokenName") String accessTokenName) {
        return accessTokenService.getSecrets(namespace, accessTokenName);
    }

}
