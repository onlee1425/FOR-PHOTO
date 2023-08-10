package com.forphoto.v1.domain.album.controller;

import com.forphoto.v1.domain.album.dto.*;
import com.forphoto.v1.domain.album.service.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/albums")
@Api(tags = "Album API")
public class AlbumController {

    private AlbumService albumService;

    @ApiOperation(value = "앨범 조회", notes = "앨범을 조회한다.")
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumInfoResponse> getAlbumInfo(@PathVariable("albumId") final long albumId) {

        AlbumInfoResponse album = albumService.getAlbum(albumId);

        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @ApiOperation(value = "앨범 생성", notes = "앨범을 생성한다.")
    @PostMapping
    public ResponseEntity<CreateAlbumResponse> createAlbum(@RequestBody String albumName) {

        CreateAlbumResponse response = albumService.createAlbum(albumName);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "앨범 목록 조회", notes = "생성된 앨범의 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<List<AlbumListResponse>> getAlbumList(@RequestParam(value = "keyword", required = false,defaultValue = "") final String keyword,
                                                                @RequestParam(value = "sort", required = false,defaultValue = "byDate") final String sort) {

        List<AlbumListResponse> AlbumList = albumService.getAlbumList(keyword,sort);

        return new ResponseEntity<>(AlbumList, HttpStatus.OK);

    }
}
