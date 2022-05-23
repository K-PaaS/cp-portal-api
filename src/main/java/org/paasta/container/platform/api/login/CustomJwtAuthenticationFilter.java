package org.paasta.container.platform.api.login;

import io.jsonwebtoken.ExpiredJwtException;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.MethodHandler;
import org.paasta.container.platform.api.common.RequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.paasta.container.platform.api.common.Constants.CHECK_Y;

/**
 * Custom Jwt Authentication Filter 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Component
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtTokenUtil;

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandler.class);

	@Value("${server.auth.valid}")
	private String AuthTokenValid;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try{

			RequestWrapper requestWrapper = new RequestWrapper(request);

			String jwtToken = jwtTokenUtil.extractJwtFromRequest(request);

			String agent = requestWrapper.getHeader("User-Agent");
			String clientIp = requestWrapper.getHeader("HTTP_X_FORWARDED_FOR");
			if (null == clientIp || clientIp.length() == 0 || clientIp.toLowerCase().equals("unknown")) {
				clientIp = requestWrapper.getHeader("REMOTE_ADDR");
			}
			if (null == clientIp || clientIp.length() == 0 || clientIp.toLowerCase().equals("unknown")) {
				clientIp = request.getRemoteAddr();
			}

			if (StringUtils.hasText(jwtToken) && jwtTokenUtil.validateToken(jwtToken)) {
				UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(jwtToken), "",
						jwtTokenUtil.getRolesFromToken(jwtToken));

				String tokenIp = jwtTokenUtil.getClientIpFromToken(jwtToken);

				if(AuthTokenValid.equals(CHECK_Y)) {
					if (clientIp.equals(tokenIp) && agent.indexOf("Java") >= 0) {
						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					} else {
						LOGGER.info("The connection information is different.");
						LOGGER.warn("agent: {} || clientIp: {} || tokenIp {}", CommonUtils.loggerReplace(agent), CommonUtils.loggerReplace(clientIp), CommonUtils.loggerReplace(tokenIp));
					}
				}else{
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}

			} else {
				LOGGER.info("Cannot set the Security Context");
			}
		} catch (ExpiredJwtException ex) {
			RequestWrapper requestWrapper = new RequestWrapper(request);
			String isRefreshToken = requestWrapper.getHeader("isRefreshToken");
			String requestURL = request.getRequestURL().toString();
			// allow for Refresh Token creation if following conditions are true.
			if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
				jwtTokenUtil.allowForRefreshToken(ex, request);
			} else
				request.setAttribute("exception", ex);

		} catch (BadCredentialsException ex) {
			request.setAttribute("exception", ex);
		} catch (Exception ex) {
			LOGGER.info(CommonUtils.loggerReplace(ex.getMessage()));
		}
		chain.doFilter(request, response);
	}


}