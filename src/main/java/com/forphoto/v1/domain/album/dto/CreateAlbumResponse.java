package com.forphoto.v1.domain.album.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateAlbumResponse {

    private Long albumId;
    private String albumName;
    private Date createdAt;
    private int count;
}
