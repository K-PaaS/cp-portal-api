package org.paasta.container.platform.api.login;

import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.login.support.PortalGrantedAuthority;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom Authentication Provider 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    private CustomUserDetailsService customUserDetailsService;
    private UsersService usersService;
    private AccessTokenService accessTokenService;

    public CustomAuthenticationProvider(CustomUserDetailsService customUserDetailsService, UsersService usersService, AccessTokenService accessTokenService) {
        this.customUserDetailsService = customUserDetailsService;
        this.usersService = usersService;
        this.accessTokenService = accessTokenService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new InternalAuthenticationServiceException(MessageConstant.ID_PASSWORD_REQUIRED.getMsg());
        }

        String userId = authentication.getPrincipal().toString(); //USER ID
        String userAuthId = authentication.getCredentials().toString(); //USER AUTH ID
        List<GrantedAuthority> list = (List<GrantedAuthority>) authentication.getAuthorities();
        String userType = list.get(0).getAuthority(); // USER TYPE

        if( userId == null || userId.length() < 1) {
            throw new AuthenticationCredentialsNotFoundException(MessageConstant.ID_REQUIRED.getMsg());
        }

        if( userAuthId == null || userAuthId.length() < 1) {
            throw new AuthenticationCredentialsNotFoundException(MessageConstant.AUTH_ID_REQUIRED.getMsg());
        }

        UserDetails loadedUser = customUserDetailsService.loadUserByUsername(userId);

        Params params = new Params();
        params.setUserAuthId(userAuthId);
        params.setUserType(userType);
        UsersList loadedUsersList = usersService.getMappingClustersAndNamespacesListByUser(params);

        List<GrantedAuthority> portalGrantedAuthorityList = new ArrayList<>();
        if(loadedUsersList.getItems().stream().map(Users::getUserType).collect(Collectors.toList()).contains(Constants.AUTH_CLUSTER_ADMIN))
            portalGrantedAuthorityList.add(new SimpleGrantedAuthority(Constants.AUTH_CLUSTER_ADMIN));
        else portalGrantedAuthorityList.add(list.get(0));


        loadedUsersList.getItems().forEach(x -> {
            Params vaultResult;
            LOGGER.info("authenticate, loadedUsers: " + x);
            try {
                vaultResult = accessTokenService.getVaultSecrets(new Params(x.getClusterId(), userAuthId, x.getUserType(), x.getCpNamespace()));
            } catch (Exception e) {
                LOGGER.info("error from vault VaultSecrets, x: " + x);
                return;
            }
            String id = x.userType.equals(Constants.AUTH_USER) ? x.getCpNamespace() : x.getClusterId();
            String type = x.userType.equals(Constants.AUTH_USER) ? Constants.ContextType.NAMESPACE.name() : Constants.ContextType.CLUSTER.name();
            portalGrantedAuthorityList.add(new PortalGrantedAuthority(id, type, x.userType, vaultResult.getClusterToken(), vaultResult.getClusterApiUrl()));
        });


        portalGrantedAuthorityList.forEach(x -> LOGGER.info("##IN AUTHENTICATE:: JWT Token Set, portalGrantedAuthorityList: " + x));

//        Collection<? extends GrantedAuthority> authorities = loadedUser.getAuthorities();
        Collection<? extends GrantedAuthority> authorities = portalGrantedAuthorityList;

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(MessageConstant.INVALID_LOGIN_INFO.getMsg());
        }

        if (!loadedUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.UNAVAILABLE_ID.getMsg());
        }
        if (!loadedUser.isEnabled()) {
            throw new DisabledException(MessageConstant.UNAVAILABLE_ID.getMsg());
        }
        if (!loadedUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.UNAVAILABLE_ID.getMsg());
        }
        if (!userAuthId.equals(loadedUser.getPassword())) {
            throw new BadCredentialsException(MessageConstant.INVALID_LOGIN_INFO.getMsg());
        }
        if (!loadedUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.UNAVAILABLE_ID.getMsg());
        }
//        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(loadedUser, null, authorities);
        result.setDetails(authentication.getDetails());

        LOGGER.info("authenticate END, result : " + CommonUtils.loggerReplace(result));
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    } }

