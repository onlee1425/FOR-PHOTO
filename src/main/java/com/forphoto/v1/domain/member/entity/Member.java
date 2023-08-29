package com.forphoto.v1.domain.member.entity;

import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.member.model.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true, nullable = false)
    private long memberId;

    private String name;

    private String password;

    private String email;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "login_At")
    @CreationTimestamp
    private Date loginAt;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();


}
