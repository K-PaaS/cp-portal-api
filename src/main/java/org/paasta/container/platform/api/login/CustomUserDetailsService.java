package org.paasta.container.platform.api.login;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

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
    public Object createAuthenticationResponse(Authentication authentication, Params params) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (!roles.contains(new SimpleGrantedAuthority(Constants.AUTH_SUPER_ADMIN))) {
            params.setUserType(Constants.AUTH_USER);
            UsersList usersList = usersService.getMappingClustersListByUser(params);
            if (usersList.getItems().size() < 1) {
                //inactive user
                return new AuthenticationResponse(Constants.RESULT_STATUS_FAIL, MessageConstant.LOGIN_INACTIVE_USER.getMsg(), CommonStatusCode.FORBIDDEN.getCode(),
                        MessageConstant.INACTIVE_USER_ACCESS.getMsg(), Constants.NULL_REPLACE_TEXT,  Constants.NULL_REPLACE_TEXT,Constants.NULL_REPLACE_TEXT,
                        Constants.NULL_REPLACE_TEXT, Constants.NULL_REPLACE_TEXT, false);
            }
            // cluster-admin 권한 포함되었는지 확인
            List<Users> clusterAdminCheck = usersList.getItems().stream().filter(x -> x.getUserType().matches(Constants.AUTH_CLUSTER_ADMIN)).collect(Collectors.toList());
            if (clusterAdminCheck.size() > 0) {
                params.setUserType(Constants.AUTH_CLUSTER_ADMIN);
            }
        }

        String token = jwtUtil.generatePortalToken(userDetails, params);
        return new AuthenticationResponse(Constants.RESULT_STATUS_SUCCESS, MessageConstant.LOGIN_SUCCESS.getMsg(), CommonStatusCode.OK.getCode(),
                MessageConstant.LOGIN_SUCCESS.getMsg(), params.getUserId(), params.getUserAuthId(), params.getUserType(), token, "cp-cluster", params.getIsSuperAdmin());
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
     * @param userId the userId
     * @return the users detail
     */
    public Users getUsersDetailsForLogin(String userId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DETAIL_LOGIN.replace("{userId:.+}", userId)
                , HttpMethod.GET, null, Users.class);
    }

}