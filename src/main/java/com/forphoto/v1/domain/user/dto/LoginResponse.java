package com.forphoto.v1.domain.user.dto;

import org.springframework.http.HttpHeaders;


public class LoginResponse {

    private final HttpHeaders headers;
    private final Object body;

    public LoginResponse(HttpHeaders headers, Object body) {
        this.headers = headers;
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }
}
