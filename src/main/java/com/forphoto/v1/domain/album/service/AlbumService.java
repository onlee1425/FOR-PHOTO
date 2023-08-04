package com.forphoto.v1.domain.album.service;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

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
}
