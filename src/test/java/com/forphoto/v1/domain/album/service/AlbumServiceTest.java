package com.forphoto.v1.domain.album.service;

import com.forphoto.v1.domain.album.dto.AlbumInfoResponse;
import com.forphoto.v1.domain.album.dto.CreateAlbumResponse;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void createAlbum() {

        // Given
        String albumName = "테스트앨범";
        Album mockAlbum = Album.builder()
                .albumId(1L)
                .albumName(albumName)
                .createdAt(new Date())
                .build();

        // Mock the repository behavior
        AlbumRepository albumRepositoryMock = mock(AlbumRepository.class);
        when(albumRepositoryMock.findByAlbumName(albumName)).thenReturn(new ArrayList<>());
        when(albumRepositoryMock.save(Mockito.any(Album.class))).thenReturn(mockAlbum);

        PhotoRepository photoRepositoryMock = mock(PhotoRepository.class);
        when(photoRepositoryMock.save(Mockito.any(Photo.class))).thenReturn(new Photo());

        AlbumService albumService = new AlbumService(albumRepositoryMock, photoRepositoryMock);

        // When
        CreateAlbumResponse response = albumService.createAlbum(albumName);


        // Then
        assertNotNull(response);
        assertEquals(mockAlbum.getAlbumId(), response.getAlbumId());
        assertEquals(mockAlbum.getAlbumName(), response.getAlbumName());
        assertEquals(mockAlbum.getCreatedAt(), response.getCreatedAt());
        assertEquals(0, response.getCount());

    }

    @Test
    void testAlbumRepository() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("테스트1앨범");
        album2.setAlbumName("테스트2앨범");

        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(1);
        albumRepository.save(album2);

        // 최신순 정렬
        List<Album> resultDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("테스트");

        // 앨범명 정렬
        List<Album> resultName = albumRepository.findByAlbumNameContainingOrderByAlbumNameDesc("테스트");

        assertEquals(resultDate.size(), resultName.size());
        for (int i = 0; i < resultDate.size(); i++) {
            assertEquals(resultDate.get(i).getAlbumName(), resultName.get(i).getAlbumName());
        }
    }
}