package com.forphoto.v1.domain.user.controller;

import com.forphoto.v1.domain.user.dto.LoginRequest;
import com.forphoto.v1.domain.user.dto.LoginResponse;
import com.forphoto.v1.domain.user.dto.RegisterRequest;
import com.forphoto.v1.domain.user.entity.User;
import com.forphoto.v1.domain.user.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<User> userRegister(@RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(user);
    }

    @ApiOperation(value = "로그인", notes = "사용자의 이메일과 비밀번호로 로그인한다.")
    @PostMapping("/login")
    public ResponseEntity<Object> userLogin(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getBody());
    }
}
