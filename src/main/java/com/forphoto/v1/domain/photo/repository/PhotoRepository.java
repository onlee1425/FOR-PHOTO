package com.forphoto.v1.domain.photo.repository;

import com.forphoto.v1.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    int countByAlbum_AlbumId(Long AlbumId);

    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAt(Long AlbumId);

}
