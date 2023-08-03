package com.forphoto.v1.domain.album.mapper;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.entity.Album;

public class AlbumMapper {

    public static AlbumInfoResponse convertToDto(Album album){

        AlbumInfoResponse response = new AlbumInfoResponse();
        response.setAlbumId(album.getAlbumId());
        response.setAlbumName(album.getAlbumName());
        response.setCreatedAt(album.getCreatedAt());

        return response;
    }
}
