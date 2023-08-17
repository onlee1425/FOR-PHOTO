package com.forphoto.v1.domain.photo.controller;

import com.forphoto.v1.domain.photo.dto.DeletePhotoResponse;
import com.forphoto.v1.domain.photo.dto.PhotoDto;
import com.forphoto.v1.domain.photo.service.PhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/albums/{albumId}/photos")
@Api(tags = "Photo API")
public class PhotoController {

    private PhotoService photoService;

    @ApiOperation(value = "사진을 업로드 한다.")
    @PostMapping
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final Long albumId,
                                                       @RequestParam("photos") MultipartFile[] files) {
        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.savePhoto(file, albumId);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    @ApiOperation(value = "사진 상세정보를 조회한다.")
    @GetMapping(value = "/{photoId}")
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable("photoId") final Long photoId) {

        PhotoDto photoDto = photoService.getPhotoInfo(photoId);
        return new ResponseEntity<>(photoDto, HttpStatus.OK);

    }

    @ApiOperation(value = "사진을 삭제한다.")
    @DeleteMapping
    public ResponseEntity<List<DeletePhotoResponse>> deletePhotos(@RequestBody List<Long> photoIds) {

        List<DeletePhotoResponse> responses = new ArrayList<>();

        for (Long photoId : photoIds) {
            DeletePhotoResponse result = photoService.deletePhoto(photoId);
            responses.add(result);
        }

        return ResponseEntity.ok(responses);
    }
}
