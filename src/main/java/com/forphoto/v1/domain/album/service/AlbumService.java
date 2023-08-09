package com.forphoto.v1.domain.album.service;

import com.forphoto.v1.common.Constants;
import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.dto.AlbumListResponse;
import com.forphoto.v1.domain.album.dto.CreateAlbumResponse;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    public AlbumInfoResponse getAlbum(Long albumId) {
        Optional<Album> result = albumRepository.findById(albumId);

        if (result.isPresent()) {
            Album album = result.get();
            AlbumInfoResponse response = new AlbumInfoResponse();
            response.setAlbumId(album.getAlbumId());
            response.setAlbumName(album.getAlbumName());
            response.setCreatedAt(album.getCreatedAt());
            response.setCount(photoRepository.countByAlbum_AlbumId(albumId));

            return response;
        } else {
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다", albumId));
        }
    }

    public CreateAlbumResponse createAlbum(String albumName) {
        List<Album> existAlbumName = albumRepository.findByAlbumName(albumName);
        if (!existAlbumName.isEmpty()) {
            throw new IllegalArgumentException("중복된 앨범명입니다.");
        }

        Album album = Album.builder()
                .albumName(albumName)
                .createdAt(new Date())
                .build();

        Album createdAlbum = albumRepository.save(album);

        CreateAlbumResponse response = new CreateAlbumResponse();
        response.setAlbumId(createdAlbum.getAlbumId());
        response.setAlbumName(createdAlbum.getAlbumName());
        response.setCreatedAt(createdAlbum.getCreatedAt());
        response.setCount(0);

        return response;
    }

    public List<AlbumListResponse> getAlbumList(String keyword,String sort){
        List<Album> albums;
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
}
