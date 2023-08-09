package com.forphoto.v1.domain.album.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AlbumListResponse {

    private Long albumId;
    private String albumName;
    private Date createdAt;
    private int count;
    private List<String> thumbUrl;

}
