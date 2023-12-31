package com.forphoto.v1.domain.photo.repository;

import com.forphoto.v1.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    int countByAlbum_AlbumId(Long albumId);

    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAt(Long albumId);

    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);

    List<Photo> findByFileNameContainingAndAlbum_AlbumIdOrderByFileNameAsc(String keyword,Long albumId);

    List<Photo> findByFileNameContainingAndAlbum_AlbumIdOrderByUploadedAt(String keyword,Long albumId);

}
