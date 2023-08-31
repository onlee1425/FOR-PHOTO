package com.forphoto.v1.security.jwt;

import com.forphoto.v1.security.springSecurity.UserDetail.CustomMemberDetailService;
import com.forphoto.v1.security.springSecurity.UserDetail.CustomMemberDetails;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CustomMemberDetailService memberDetailService;
    @Value("${jwt.token.key}")
    private String secretKey;

    public JwtToken generateToken(Long id){
        CustomMemberDetails memberDetails = memberDetailService.loadUserByUsername(String.valueOf(id));
        List<String> authorities = memberDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Date issuedAt = new Date();
        long now = new Date().getTime();

        Date accessTokenExpiredIn = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(memberDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(accessTokenExpiredIn)
                .claim("ROLE", authorities)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        Date refreshTokenExpiredIn = new Date(now + JwtProperties.REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setSubject(memberDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(refreshTokenExpiredIn)
                .claim("ROLE", authorities)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(JwtProperties.REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        CustomMemberDetails memberDetails = memberDetailService.loadUserByUsername(claims.getSubject());
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("ROLE").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);
    }
    public Claims parseClaims(String accessToken) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(accessToken)
                .getBody();
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getExpiration();

        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("validate 접근");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("토큰예외 : ExpiredJwtException");
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("토큰예외 : MalformedJwtException");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("토큰예외 : UnsupportedJwtException");
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("토큰예외 : IllegalArgumentException");
            throw e;
        } catch (SignatureException e) {
            log.warn("토큰예외 : SignatureException");
            throw e;
        }
    }

}
