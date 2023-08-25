package com.forphoto.v1.security.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtToken {

    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpirationTime;

}
