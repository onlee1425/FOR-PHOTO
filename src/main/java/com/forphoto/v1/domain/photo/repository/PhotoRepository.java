package com.forphoto.v1.domain.photo.repository;

import com.forphoto.v1.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    int countByAlbum_AlbumId(Long AlbumId);

}
