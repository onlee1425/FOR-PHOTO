package com.forphoto.v1.domain.member.controller;

import com.forphoto.v1.domain.member.dto.LoginRequest;
import com.forphoto.v1.domain.member.dto.LoginResponse;
import com.forphoto.v1.domain.member.dto.RegisterRequest;
import com.forphoto.v1.domain.member.entity.Member;
import com.forphoto.v1.domain.member.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
