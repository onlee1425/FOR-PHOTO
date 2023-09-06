package com.forphoto.v1.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isTokenLoggedOut(String token) {
        String logoutKey = "logout:" + token;
        Boolean isLoggedOut = redisTemplate.hasKey(logoutKey);
        return isLoggedOut != null && isLoggedOut;
    }

    public void deleteRefreshToken(Long memberId) {
        redisTemplate.delete("memberId : " + memberId);
    }

    public void setBlackListToken(Long memberId, String accessToken, long expiration) {
        if (expiration > 0) {
            redisTemplate.opsForValue().set("logout : " + accessToken, String.valueOf(memberId), Duration.ofMillis(expiration));
        }
    }
}
