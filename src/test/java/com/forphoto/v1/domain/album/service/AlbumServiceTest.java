package com.forphoto.v1.domain.album.service;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private PhotoRepository photoRepository;


    @Test
    void getAlbum() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumInfoResponse resultAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("테스트", resultAlbum.getAlbumName());
    }

    @Test
    void testPhotoCount() {
        // Given
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(savedAlbum);
        photoRepository.save(photo1);

        // When
        AlbumInfoResponse resultAlbum = albumService.getAlbum(savedAlbum.getAlbumId());

        // Then
        assertNotNull(resultAlbum);
        assertEquals("테스트", resultAlbum.getAlbumName());
        assertEquals(1, resultAlbum.getCount());
    }
}