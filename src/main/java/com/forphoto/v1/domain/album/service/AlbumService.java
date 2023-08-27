package com.forphoto.v1.domain.album.service;

import com.forphoto.v1.common.Constants;
import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.dto.AlbumListResponse;
import com.forphoto.v1.domain.album.dto.CreateAlbumResponse;
import com.forphoto.v1.domain.album.dto.UpdateAlbumNameResponse;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.member.entity.Member;
import com.forphoto.v1.domain.member.repository.MemberRepository;
import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final MemberRepository memberRepository;

    public AlbumInfoResponse getAlbum(Long albumId,Long memberId) {
        Long albumMemberId = albumRepository.findMemberIdByAlbumId(albumId);

        if (albumMemberId != null && albumMemberId.equals(memberId)) {
            Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("앨범이 조회되지 않았습니다"));

            AlbumInfoResponse response = new AlbumInfoResponse();
            response.setAlbumId(album.getAlbumId());
            response.setAlbumName(album.getAlbumName());
            response.setCreatedAt(album.getCreatedAt());
            response.setCount(photoRepository.countByAlbum_AlbumId(albumId));

            return response;
        } else {
            throw new IllegalArgumentException("앨범이 해당 멤버의 것이 아닙니다");
        }
    }

    public CreateAlbumResponse createAlbum(String albumName,Long memberId) {
        List<Album> existAlbumName = albumRepository.findByAlbumName(albumName);
        if (!existAlbumName.isEmpty()) {
            throw new IllegalArgumentException("중복된 앨범명입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다"));

        Album album = Album.builder()
                .albumName(albumName)
                .createdAt(new Date())
                .member(member)
                .build();

        Album createdAlbum = albumRepository.save(album);

        CreateAlbumResponse response = new CreateAlbumResponse();
        response.setAlbumId(createdAlbum.getAlbumId());
        response.setAlbumName(createdAlbum.getAlbumName());
        response.setCreatedAt(createdAlbum.getCreatedAt());
        response.setCount(0);

        return response;
    }

    public List<AlbumListResponse> getAlbumList(String keyword, String sort) {
        List<Album> albums;
        log.info("키워드 = " + keyword);
        log.info("정렬 = " + sort);
        if (Objects.equals(sort, "byName")) {
            albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
        } else if (Objects.equals(sort, "byDate")) {
            albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다");
        }

        List<AlbumListResponse> response = new ArrayList<>();
        for (Album album : albums) {
            AlbumListResponse res = new AlbumListResponse();
            res.setAlbumId(album.getAlbumId());
            res.setAlbumName(album.getAlbumName());
            res.setCreatedAt(album.getCreatedAt());
            res.setCount(album.getPhotos().size());

            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAt(album.getAlbumId());
            List<String> thumbUrls = new ArrayList<>();
            for (Photo photo : top4) {
                thumbUrls.add(Constants.PATH_PREFIX + photo.getThumbUrl());
            }
            res.setThumbUrl(thumbUrls);

            response.add(res);
        }

        return response;
    }

    public UpdateAlbumNameResponse changeName(Long albumId, String albumName) {
        Optional<Album> album = this.albumRepository.findById(albumId);

        if (album.isEmpty()) {
            throw new NoSuchElementException("Album ID '%'가 존재하지 않습니다.");
        }

        Album updateAlbum = album.get();
        updateAlbum.setAlbumName(albumName);
        albumRepository.save(updateAlbum);

        UpdateAlbumNameResponse response = new UpdateAlbumNameResponse();
        response.setAlbumId(updateAlbum.getAlbumId());
        response.setAlbumName(updateAlbum.getAlbumName());
        response.setCreatedAt(updateAlbum.getCreatedAt());
        response.setCount(updateAlbum.getPhotos().size());

        return response;
    }

    public void deleteAlbum(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범ID 입니다."));

        albumRepository.delete(album);

    }
}
