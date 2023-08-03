package com.forphoto.v1.domain.album.controller;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.service.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/albums")
@Api(tags = "Album API")
public class AlbumController {

    private AlbumService albumService;

    @ApiOperation(value = "앨범 조회", notes = "앨범을 조회한다.")
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumInfoResponse> getAlbum(@PathVariable("albumId") final long albumId) {

        AlbumInfoResponse album = albumService.getAlbum(albumId);

        return new ResponseEntity<>(album, HttpStatus.OK);
    }
}
