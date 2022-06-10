package org.paasta.container.platform.api.login;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;

import org.paasta.container.platform.api.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;

/**
 * Custom User Details Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private final PropertyService propertyService;

    private final UsersService usersService;

    private final RestTemplateService restTemplateService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Instantiates a new CustomUserDetails service
     *
     * @param propertyService the property service
     * @param usersService    the users service
     */
    @Autowired
    public CustomUserDetailsService(PropertyService propertyService, UsersService usersService, RestTemplateService restTemplateService) {
        this.propertyService = propertyService;
        this.usersService = usersService;
        this.restTemplateService = restTemplateService;
    }

    @Autowired
    private HttpServletRequest request;


    /**
     * 로그인 인증을 위한 User 상세 조회(Get Users detail for login authentication)
     *
     * @param userId the user id
     * @return the user details
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;
        Users user = getUsersDetailsForLogin(userId);
        if (user != null) {
            roles = Arrays.asList(new SimpleGrantedAuthority(user.getUserType()));
            return new User(user.getUserId(), user.getUserAuthId(), roles);
        }
        throw new UsernameNotFoundException(MessageConstant.INVALID_LOGIN_INFO.getMsg());
    }


    /**
     * 사용자 인증 후 리턴 객체 생성(Create authentication response)
     *
     * @param params the params
     * @return the object
     */
    public Object createAuthenticationResponse(Params params) {

        UserDetails userdetails = loadUserByUsername(params.getUserId());
        //Generate token
        String token = jwtUtil.generateToken(userdetails, params);
        AuthenticationResponse  authResponse = new AuthenticationResponse(Constants.RESULT_STATUS_SUCCESS, MessageConstant.LOGIN_SUCCESS.getMsg(), CommonStatusCode.OK.getCode(),
                MessageConstant.LOGIN_SUCCESS.getMsg(), userdetails.getUsername(), token, "cp-cluster", params.getIsSuperAdmin());

        return authResponse;
    }


    /*
     */
/**
 * 기본 Namespace를 제외한 인증된 사용자가 속한 Namespace 목록 조회(Get List of Namespaces to which authenticated users belong, excluding default namespaces)
 *
 * @param userItem the users item
 * @return the list
 *//*

    public List<loginMetaDataItem> defaultNamespaceFilter(List<Users> userItem) {

        List<loginMetaDataItem> loginMetaData = new ArrayList<>();

        for (Users user : userItem) {

            if (!user.getUserType().equals(Constants.AUTH_CLUSTER_ADMIN)) {

                if (!user.getCpNamespace().equals(propertyService.getDefaultNamespace())) {
                    loginMetaData.add(new loginMetaDataItem(user.getCpNamespace(), user.getUserType()));
                }

            }
        }

        return loginMetaData;
    }
*/


    /**
     * Users 로그인을 위한 상세 조회(Get Users for login)
     *
     * @param userId  the userId
     * @return the users detail
     */
    public Users getUsersDetailsForLogin(String userId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DETAIL_LOGIN.replace("{userId:.+}", userId)
                , HttpMethod.GET, null, Users.class);
    }

}