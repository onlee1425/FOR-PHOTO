package com.forphoto.v1.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final RedisTemplate<String,String> redisTemplate;

    public boolean isTokenLoggedOut(String token) {
        String logoutKey = "logout:" + token;
        Boolean isLoggedOut = redisTemplate.hasKey(logoutKey);
        return isLoggedOut != null && isLoggedOut;
    }
}
