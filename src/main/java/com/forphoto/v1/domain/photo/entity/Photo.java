package com.forphoto.v1.domain.photo.entity;

import com.forphoto.v1.domain.album.entity.Album;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "photo_id",unique = true,nullable = false)
    private long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "thumb_url")
    private String thumbUrl;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "uploaded_at")
    @CreationTimestamp
    private Date uploadedAt;

    @Column(name = "file_size")
    private long fileSize;


}
