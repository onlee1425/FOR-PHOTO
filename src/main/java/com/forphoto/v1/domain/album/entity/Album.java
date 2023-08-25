package com.forphoto.v1.domain.album.entity;

import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.member.entity.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "album_id", unique = true, nullable = false)
    private long albumId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos;

    @Column(name = "album_name")
    private String albumName;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
