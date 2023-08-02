package com.forphoto.v1.domain.album.repository;

import com.forphoto.v1.domain.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long> {
    List<Album> findByAlbumName(String name);

}
