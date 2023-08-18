package com.forphoto.v1.domain.photo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovePhotosRequest {

    private Long fromAlbumId;
    private Long toAlbumId;
    List<Long> photoIds;
}
