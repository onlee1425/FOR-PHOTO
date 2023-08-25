package com.forphoto.v1.security.jwt;

import com.forphoto.v1.security.springSecurity.UserDetail.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    @Value("ENC(14PHwUGbXOMEe+LuOO5iGQNIOsrxCP5p)")
    private String secretKey;

    public JwtToken generateToken(Long id){
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(String.valueOf(id));

        Date issuedAt = new Date();
        long now = new Date().getTime();

        Date accessTokenExpiredIn = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(accessTokenExpiredIn)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        Date refreshTokenExpiredIn = new Date(now + JwtProperties.REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(refreshTokenExpiredIn)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(JwtProperties.REFRESH_TOKEN_EXPIRE_TIME)
                .build();

    }

}
