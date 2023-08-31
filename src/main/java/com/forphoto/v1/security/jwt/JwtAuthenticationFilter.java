package com.forphoto.v1.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static com.forphoto.v1.security.springSecurity.config.SpringSecurityConfig.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        log.info(requestURI);

        if (Arrays.asList(PERMIT_GET_API_ARRAY).contains(requestURI)) {
            String method = ((HttpServletRequest) request).getMethod();
            if ("GET".equals(method)) {
                chain.doFilter(request, response);
                return;
            }
        } else if (Arrays.asList(PERMIT_API_ARRAY).contains(requestURI) || Arrays.asList(PERMIT_URL_ARRAY).contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken((HttpServletRequest) request);
        log.info("JwtAuthenticationFilter : doFilter 들어옴");
        log.info("입력 토큰 : " + token);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                if (!jwtTokenUtil.isTokenLoggedOut(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                } else {
                    log.warn("이미 로그아웃된 JWT 토큰입니다, uri: {}", requestURI);
                    throw new RuntimeException();
                }
            }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtProperties.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.BEARER_TYPE)) {
            log.info("Bearer 토큰 추출 : " + bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshtoken")) {
                    log.info("쿠키에서 리프레시토큰 추출 : " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
