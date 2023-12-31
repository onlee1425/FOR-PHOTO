package com.forphoto.v1.domain.photo.controller;

import com.forphoto.v1.domain.photo.dto.MovePhotosRequest;
import com.forphoto.v1.domain.photo.dto.PhotoDto;
import com.forphoto.v1.domain.photo.dto.PhotosResponse;
import com.forphoto.v1.domain.photo.service.PhotoService;
import com.forphoto.v1.security.springSecurity.UserDetail.CustomMemberDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
                                                       @RequestParam("photos") MultipartFile[] files,
                                                       @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {
        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.savePhoto(file, albumId,memberDetails.getMemberId());
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
    public ResponseEntity<List<PhotosResponse>> deletePhotos(@RequestBody List<Long> photoIds) {

        List<PhotosResponse> responses = new ArrayList<>();

        for (Long photoId : photoIds) {
            PhotosResponse result = photoService.deletePhoto(photoId);
            responses.add(result);
        }

        return ResponseEntity.ok(responses);
    }

    @ApiOperation(value = "선택한 사진을 다른 앨범으로 옮긴다.")
    @PutMapping("/move")
    public ResponseEntity<List<PhotosResponse>> movePhotos(@RequestBody MovePhotosRequest request,
                                                           @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        List<PhotosResponse> responses = photoService.movePhotos(request,memberDetails.getMemberId());
        return ResponseEntity.ok(responses);

    }

    @ApiOperation(value = "앨범에 있는 사진 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<List<PhotosResponse>> getPhotos(@RequestParam(value = "keyword", required = false, defaultValue = "") final String keyword,
                                                          @RequestParam(value = "sort", required = false, defaultValue = "byDate") final String sort,
                                                          @PathVariable Long albumId,
                                                          @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        List<PhotosResponse> photoList = photoService.getPhotoList(keyword, sort, albumId,memberDetails.getMemberId());
        return new ResponseEntity<>(photoList, HttpStatus.OK);
    }

    @ApiOperation(value = "사진을 다운로드 한다.")
    @GetMapping("/download")
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response, @PathVariable Long albumId,
                               @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {
        try {
            if (photoIds.length == 1) {
                File file = photoService.getImageFile(photoIds[0],albumId,memberDetails.getMemberId());
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(file), outputStream);

            } else if (photoIds.length > 1) {
                File zipFile = photoService.getImageFilesWithZip(photoIds,albumId,memberDetails.getMemberId());
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
                response.setHeader("Content-Disposition", "attachment; filename=photos.zip");

                try (InputStream is = new FileInputStream(zipFile);
                     OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                zipFile.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
