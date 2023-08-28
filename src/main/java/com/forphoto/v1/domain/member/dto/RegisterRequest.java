package com.forphoto.v1.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;
    private String password;
    private String name;


//    public RegisterRequest(String email, String password, String name) {
//        this.email = email;
//        this.password = password;
//        this.name = name;
//    }

}
