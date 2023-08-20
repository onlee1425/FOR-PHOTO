package com.forphoto.v1.domain.photo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PhotosResponse {

    private Long photoId;
    private String fileName;
    private String thumbUrl;
    private Date uploadedAt;
}
