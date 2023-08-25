package com.forphoto.v1.domain.user.service;

import com.forphoto.v1.domain.user.dto.LoginRequest;
import com.forphoto.v1.domain.user.dto.LoginResponse;
import com.forphoto.v1.domain.user.dto.RegisterRequest;
import com.forphoto.v1.domain.user.entity.User;
import com.forphoto.v1.domain.user.repository.UserRepository;
import com.forphoto.v1.security.jwt.JwtToken;
import com.forphoto.v1.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String,String> redis;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("해당하는 회원이 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("올바르지 않은 비밀번호 입니다.");
        }

        JwtToken token = jwtTokenProvider.generateToken(user.getUserId());
        redis.opsForValue().set("userId : " +user.getUserId(),"Bearer "+token.getRefreshToken(),
                token.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer " + token.getAccessToken());
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .path("/api/token/reissue")
                .httpOnly(true)
                .sameSite("Strict")
                .build();
        headers.add("Set-Cookie",responseCookie.toString());
        String message = "로그인이 완료되었습니다.";

        return new LoginResponse(headers,message);
    }
}
