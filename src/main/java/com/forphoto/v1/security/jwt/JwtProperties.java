package com.forphoto.v1.security.jwt;

public interface JwtProperties {
    String AUTHORIZATION_HEADER = "Authorization";
    String BEARER_TYPE = "Bearer ";
    long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 & 1000L;
    long REFRESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L;
}
