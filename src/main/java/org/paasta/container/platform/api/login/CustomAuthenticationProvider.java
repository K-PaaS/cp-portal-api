package org.paasta.container.platform.api.login;

import org.paasta.container.platform.api.common.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Custom Authentication Provider 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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
        Collection<? extends GrantedAuthority> authorities = loadedUser.getAuthorities();

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(MessageConstant.INVALID_LOGIN_INFO.getMsg());
        }

        if(!authorities.contains(new SimpleGrantedAuthority(userType))) {
            throw new InternalAuthenticationServiceException(MessageConstant.INVALID_AUTHORITY.getMsg());
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
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    } }

