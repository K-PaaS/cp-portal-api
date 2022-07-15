package org.paasta.container.platform.api.common;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.login.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Admin Check Aspect 클래스
 * AOP - Admin check
 *
 * @author hrjin
 * @version 1.0
 * @since 2020-08-25
 **/
@Aspect
@Component
@Order(0)
public class AdminCheckAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandler.class);
    private static final String IS_ADMIN = "isAdmin";
    private static final String U_LANG_KEY = "uLang";

    @Autowired
    private HttpServletRequest request;
    /**
     * API URL 호출 시 로그인한 사용자 정보로 admin/user 판별 (check that login user is admin or user)
     *
     * true/false 를 argument 안에 파라미터로 넣어줌
     * isAdmin 으로 판별해서 true 면 admin 서비스 호출
     *
     * @param joinPoint the joinPoint
     * @return the object
     * @throws Throwable
     */
    @Around("execution(* org.paasta.container.platform.api..*Controller.*(..))" + "&& !@annotation(org.paasta.container.platform.api.config.NoAuth)")
    public Object isAdminAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        String uLang = Constants.U_LANG_KO;

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<GrantedAuthority> list = (List<GrantedAuthority>) authentication.getAuthorities();
        String authority = list.get(0).getAuthority();
        String userAuthId = userDetails.getPassword();


        LOGGER.info("YOUR AUTHORITY :: {}", CommonUtils.loggerReplace(authority));
        LOGGER.info("YOUR AUTH ID :: {}", CommonUtils.loggerReplace(userAuthId));


        Object[] args = Arrays.asList(joinPoint.getArgs()).toArray();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] sigParamNames = methodSignature.getParameterNames();

        int idx = Arrays.asList(sigParamNames).indexOf("params");


        if(idx > -1) { // parameter exist
            Params params = (Params) args[idx];
            if (params.getUserAuthId().equals(Constants.EMPTY_STRING)) {
                params.setUserAuthId(userAuthId);
            }
            if (params.getUserType().equals(Constants.EMPTY_STRING)) {
                params.setUserType(authority);
            }

            args = CommonUtils.modifyValue(args, idx, params);
        }


        //language
        RequestWrapper requestWrapper = new RequestWrapper(request);

        try {
            uLang = requestWrapper.getHeader(U_LANG_KEY).toLowerCase();
            if(uLang.equalsIgnoreCase(Constants.U_LANG_KO)) {
                uLang = Constants.U_LANG_KO;
            }
            else if(uLang.startsWith(Constants.U_LANG_KO_START_WITH)) {
                uLang = Constants.U_LANG_KO;
            }
            else {
                uLang = Constants.U_LANG_ENG;
            }
        }
        catch (Exception e) {
            uLang = Constants.U_LANG_ENG;
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(uLang);
        authentication.setDetails(customUserDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return joinPoint.proceed(args);
    }

}