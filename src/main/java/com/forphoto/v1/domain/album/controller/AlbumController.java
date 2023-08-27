package com.forphoto.v1.domain.album.controller;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.dto.AlbumListResponse;
import com.forphoto.v1.domain.album.dto.CreateAlbumResponse;
import com.forphoto.v1.domain.album.dto.UpdateAlbumNameResponse;
import com.forphoto.v1.domain.album.service.AlbumService;
import com.forphoto.v1.security.springSecurity.UserDetail.CustomMemberDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    public ResponseEntity<AlbumInfoResponse> getAlbumInfo(@PathVariable("albumId") final long albumId, @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        AlbumInfoResponse album = albumService.getAlbum(albumId,memberDetails.getMemberId());

        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @ApiOperation(value = "앨범 생성", notes = "앨범을 생성한다.")
    @PostMapping
    public ResponseEntity<CreateAlbumResponse> createAlbum(@RequestBody String albumName, @ApiIgnore @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        CreateAlbumResponse response = albumService.createAlbum(albumName,memberDetails.getMemberId());

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "앨범 목록 조회", notes = "생성된 앨범의 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<List<AlbumListResponse>> getAlbumList(@RequestParam(value = "keyword", required = false, defaultValue = "") final String keyword,
                                                                @RequestParam(value = "sort", required = false, defaultValue = "byDate") final String sort) {

        List<AlbumListResponse> AlbumList = albumService.getAlbumList(keyword, sort);

        return new ResponseEntity<>(AlbumList, HttpStatus.OK);

    }

    @ApiOperation(value = "앨범명 변경", notes = "기존에 생성된 앨범의 이름을 수정한다.")
    @PutMapping("/{albumId}")
    public ResponseEntity<UpdateAlbumNameResponse> updateAlbumName(@PathVariable("albumId") final long albumId,
                                                                   @RequestBody String updateAlbumName) {

        UpdateAlbumNameResponse updateAlbum = albumService.changeName(albumId, updateAlbumName);

        return new ResponseEntity<>(updateAlbum, HttpStatus.OK);
    }

    @ApiOperation(value = "앨범 삭제", notes = "앨범을 삭제한다.")
    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable("albumId") final long albumId) {

        albumService.deleteAlbum(albumId);

    }

}
