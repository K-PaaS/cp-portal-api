package org.container.platform.api.login;


import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.MessageConstant;
import org.container.platform.api.common.model.CommonStatusCode;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.config.NoAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Login Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Api(value = "LoginController v1")
@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired @Qualifier("jwtUtil")
    private JwtUtil jwtTokenUtil;

    /**
     * 사용자 로그인(User login)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "사용자 로그인(User login)", nickname = "userLogin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authRequest", value = "로그인을 위한 사용자 정보", required = true, dataType = "object", paramType = "body", dataTypeClass = Params.class)
    })
    @NoAuth
    @PostMapping("/login")
    @ResponseBody
    public Object userLogin(@RequestBody Params params) {

        Authentication authentication = null;

        try {

            List<GrantedAuthority> authorities = new ArrayList<>();

            if (params.getIsSuperAdmin()) {
                authorities.add(new SimpleGrantedAuthority(Constants.AUTH_SUPER_ADMIN));
                params.setUserType(Constants.AUTH_SUPER_ADMIN);
            } else {
                authorities.add(new SimpleGrantedAuthority(Constants.AUTH_USER));
            }

            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    params.getUserId(), params.getUserAuthId(), authorities));

        } catch (Exception e) {
            return new ResultStatus(Constants.RESULT_STATUS_FAIL, MessageConstant.LOGIN_FAIL.getMsg(),
                    CommonStatusCode.UNAUTHORIZED.getCode(), e.getMessage());
        }

        return userDetailsService.createAuthenticationResponse(authentication, params);
    }



    @NoAuth
    @GetMapping(value = "/refreshtoken")
    @ResponseBody
    public Object refreshtoken(HttpServletRequest request) throws Exception {

        AuthenticationResponse  authResponse = null;

        try {
            // From the HttpRequest get the claims
            DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");
            Map<String, Object> expectedMap = jwtTokenUtil.getMapFromIoJsonwebtokenClaims(claims);
            String token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
            authResponse = new AuthenticationResponse(Constants.RESULT_STATUS_SUCCESS, MessageConstant.REFRESH_TOKEN_SUCCESS.getMsg(), CommonStatusCode.OK.getCode(),
                    MessageConstant.REFRESH_TOKEN_SUCCESS.getMsg(), token);
        }

        catch (Exception e){
            authResponse = new AuthenticationResponse(Constants.RESULT_STATUS_FAIL, MessageConstant.REFRESH_TOKEN_FAIL.getMsg(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                    MessageConstant.REFRESH_TOKEN_FAIL.getMsg(), Constants.NULL_REPLACE_TEXT);
        }


       return authResponse;
    }

}