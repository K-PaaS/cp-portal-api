package org.paasta.container.platform.api.login;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.RequestWrapper;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * JwtUtil 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Service
public class JwtUtil {

    private String secret;
    public static int jwtExpirationInMs;
    public static int refreshExpirationDateInMs;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Value("${jwt.refreshExpirationDateInMs}")
    public void setRefreshExpirationDateInMs(int refreshExpirationDateInMs) { this.refreshExpirationDateInMs = refreshExpirationDateInMs; }

    /**
     * JWT 토큰 생성을 위한 권한 및 브라우저 정보 조회(Get authority and browser info for generate JWT token)
     *
     * @param userDetails      the user details
     * @param authRequest      the auth request
     * @param userListByUserId the users list
     * @return the string
     */
    public String generateToken(UserDetails userDetails, AuthenticationRequest authRequest, UsersList userListByUserId) {
        Map<String, Object> claims = new HashMap<>();
        String url = null;
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority(Constants.AUTH_CLUSTER_ADMIN))) {
            claims.put("isClusterAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.AUTH_NAMESPACE_ADMIN))) {
            claims.put("isNamespaceAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.AUTH_USER))) {
            claims.put("isUser", true);
        }

        claims.put("IP", authRequest.getClientIp());
        claims.put("Browser", authRequest.getBrowser());
        for (Users users : userListByUserId.getItems())
            claims.put("url", users.getClusterApiUrl());

        return doGenerateToken(claims, userDetails.getUsername());
    }


    /**
     * JWT 토큰 생성(Generate JWT token)
     *
     * @param claims  the claims
     * @param subject the subject
     * @return the string
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }


    /**
     * 토큰 유효성 검사(Validation token value)
     *
     * @param authToken the auth token
     * @return the boolean
     */
    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException(MessageConstant.LOGIN_INVALID_CREDENTIALS.getMsg(), ex);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), MessageConstant.LOGIN_TOKEN_EXPIRED.getMsg(), ex);
        }
    }


    /**
     * 토큰을 통한 사용자 이름 조회(Get User name from token)
     *
     * @param token the token
     * @return the string
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        return claims.getSubject();
    }


    /**
     * 토큰을 통한 권한 조회(Get Roles from token)
     *
     * @param authToken the auth token
     * @return the list
     */
    public List<SimpleGrantedAuthority> getRolesFromToken(String authToken) {
        List<SimpleGrantedAuthority> roles = null;
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
        Boolean isClusterAdmin = claims.get("isClusterAdmin", Boolean.class);
        Boolean isNamespaceAdmin = claims.get("isNamespaceAdmin", Boolean.class);
        Boolean isUser = claims.get("isUser", Boolean.class);

        if (isClusterAdmin != null && isClusterAdmin == true) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Constants.AUTH_CLUSTER_ADMIN));
        }

        if (isNamespaceAdmin != null && isNamespaceAdmin == true) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Constants.AUTH_NAMESPACE_ADMIN));
        }

        if (isUser != null && isUser == true) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Constants.AUTH_USER));
        }
        return roles;
    }


    /**
     * 토큰을 통한 클라이언트 IP 조회(Get Client IP from token)
     *
     * @param authToken the auth token
     * @return the string
     */
    public String getClientIpFromToken(String authToken) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
        String clientIp = String.valueOf(claims.get("IP"));

        return clientIp;
    }


    /**
     * API 요청으로부터 JWT 토큰 추출(Extract JWT token from API Request)
     *
     * @param request the request
     * @return the string
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        RequestWrapper requestWrapper = new RequestWrapper(request);

        String bearerToken = requestWrapper.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }



    /**
     * 관리자 사용자 포탈 접속 용
     * JWT 토큰 생성을 위한 권한 및 브라우저 정보 조회 (Get authority and browser info for generate JWT token for admin access user portal)
     *
     * @param userDetails      the user details
     * @param authRequest      the auth request
     * @param userListByUserId the users list
     * @return the string
     */
    public String generateTokenForAdminToAccessUserPortal(UserDetails userDetails, AuthenticationRequest authRequest, UsersList userListByUserId) {
        Map<String, Object> claims = new HashMap<>();
        String url = null;
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        claims.put("isUser", true);

        claims.put("IP", authRequest.getClientIp());
        claims.put("Browser", authRequest.getBrowser());
        for (Users users : userListByUserId.getItems())
            claims.put("url", users.getClusterApiUrl());

        return doGenerateToken(claims, userDetails.getUsername());
    }



    /**
     * Refresh JWT 토큰 생성(Generate Refresh JWT token)
     *
     * @param claims  the claims
     * @param subject the subject
     * @return the string
     */
    public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationDateInMs))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    /**
     * Refresh JWT 허가(Allow Refresh JWT token)
     *
     * @param ex  the ExpiredJwtException
     * @param request the HttpServletRequest
     * @return the string
     */
    public void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {

        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create
        // new JWT
        request.setAttribute("claims", ex.getClaims());
    }

    /**
     * JWT Claims Map 조회 (Get map from JWT token claims)
     *
     * @param claims  the DefaultClaims
     * @return the Map
     */
    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}
