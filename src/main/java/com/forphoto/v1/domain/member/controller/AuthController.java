package com.forphoto.v1.domain.member.controller;

import com.forphoto.v1.domain.member.dto.LoginRequest;
import com.forphoto.v1.domain.member.dto.LoginResponse;
import com.forphoto.v1.domain.member.dto.RegisterRequest;
import com.forphoto.v1.domain.member.entity.Member;
import com.forphoto.v1.domain.member.service.AuthService;
import com.forphoto.v1.security.springSecurity.UserDetail.CustomMemberDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
@Api(tags = "Auth API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "회원 가입", notes = "회원 가입을 진행한다.")
    @PostMapping("/register")
    public ResponseEntity<Member> memberRegister(@RequestBody RegisterRequest request) {

        Member member = authService.register(request);

        return ResponseEntity.ok(member);
    }

    @ApiOperation(value = "로그인", notes = "사용자의 이메일과 비밀번호로 로그인한다.")
    @PostMapping("/login")
    public ResponseEntity<Object> memberLogin(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getBody());
    }

    @ApiOperation(value = "로그아웃", notes = "사용자의 토큰을 로그아웃 처리한다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@ApiIgnore @RequestHeader("Authorization") String accessToken,
                                         @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails,
                                         HttpServletResponse response) {

        authService.logout(accessToken, memberDetails.getMemberId(), memberDetails.getAuthorities().toString());
        authService.deleteRefreshTokenCookie(response);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @ApiOperation(value = "액세스 토큰 갱신", notes = "액세스 토큰 만료시, 리프레시 토큰을 이용하여 액세스 토큰을 재발급 한다.")
    @PostMapping("/token/reissue")
    public HttpHeaders tokenReissue(@ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        return authService.tokenReissue(memberDetails.getMemberId(), memberDetails.getAuthorities().toString());
    }

}
