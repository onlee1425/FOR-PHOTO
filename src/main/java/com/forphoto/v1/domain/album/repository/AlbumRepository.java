package com.forphoto.v1.domain.album.repository;

import com.forphoto.v1.domain.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long> {
    Long findMemberIdByAlbumId(Long albumId);
    @Query("SELECT a FROM Album a WHERE a.member.memberId = :memberId")
    List<Album> findByMemberId(@Param("memberId") Long memberId);
}
