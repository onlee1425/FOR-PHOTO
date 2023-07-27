package com.forphoto.v1.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private long userId;

    private String name;

    private String password;

    private String email;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "login_At")
    @CreationTimestamp
    private Date loginAt;


}
