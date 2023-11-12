package com.forphoto.v1.domain.member.service;

import com.forphoto.v1.domain.member.dto.LoginRequest;
import com.forphoto.v1.domain.member.dto.LoginResponse;
import com.forphoto.v1.domain.member.dto.RegisterRequest;
import com.forphoto.v1.domain.member.entity.Member;
import com.forphoto.v1.domain.member.model.MemberRole;
import com.forphoto.v1.domain.member.repository.MemberRepository;
import com.forphoto.v1.security.jwt.JwtToken;
import com.forphoto.v1.security.jwt.JwtTokenProvider;
import com.forphoto.v1.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redis;
    private final JwtTokenUtil jwtTokenUtil;

    public Member register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException();
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .memberRole(MemberRole.MEMBER)
                .build();

        return memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmailAndProvider(request.getEmail(),request.getProvider())
                .orElseThrow(() -> new RuntimeException("해당하는 회원이 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("올바르지 않은 비밀번호 입니다.");
        }

        JwtToken token = jwtTokenProvider.generateToken(member.getMemberId());
        redis.opsForValue().set("memberId : " + member.getMemberId(), "Bearer " + token.getRefreshToken(),
                token.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer " + token.getAccessToken());
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .path("/api/user/token/reissue")
                .httpOnly(true)
                .sameSite("Strict")
                .build();
        headers.add("Set-Cookie", responseCookie.toString());
        String message = "로그인이 완료되었습니다.";

        return new LoginResponse(headers, message);
    }

    public void logout(String accessToken, Long memberId, String role) {
        accessToken = accessToken.replace("Bearer ", "");
        log.info(role);
        if (role.equals("[MEMBER]")) {
            log.info("멤버 로그아웃 진행");
            if (redis.opsForValue().get("memberId : " + memberId) != null) {
                Long expiration = jwtTokenProvider.getExpiration(accessToken);
                jwtTokenUtil.setBlackListToken(memberId, accessToken, expiration);
                jwtTokenUtil.deleteRefreshToken(memberId);
            }
        }

    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/api/user/token/reissue");

        response.addCookie(refreshTokenCookie);
    }

    public HttpHeaders tokenReissue(Long memberId, String role) {
        HttpHeaders headers = new HttpHeaders();

        if (role.equals("[MEMBER]")) {
            JwtToken token = jwtTokenProvider.generateToken(memberId);
            redis.opsForValue().set("memberId : " + memberId, token.getRefreshToken(),
                    token.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

            headers.add("authorization", "Bearer " + token.getAccessToken());
            ResponseCookie cookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                    .path("/api/user/token/reissue")
                    .httpOnly(true)
                    .build();
            headers.add("Set-Cookie",cookie.toString());

            return headers;
        } else {
            throw new RuntimeException("유효하지 않은 회원입니다.");
        }
    }
}
