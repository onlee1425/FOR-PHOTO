package com.forphoto.v1.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

        private String email;
        private String password;
        private String name;

}
