package com.forphoto.v1.domain.photo.service;

import com.forphoto.v1.common.Constants;
import com.forphoto.v1.domain.album.entity.Album;
import com.forphoto.v1.domain.album.repository.AlbumRepository;
import com.forphoto.v1.domain.photo.dto.MovePhotosRequest;
import com.forphoto.v1.domain.photo.dto.PhotosResponse;
import com.forphoto.v1.domain.photo.dto.PhotoDto;
import com.forphoto.v1.domain.photo.entity.Photo;
import com.forphoto.v1.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;

    public PhotoDto savePhoto(MultipartFile file, Long albumId) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null){
            throw new IllegalArgumentException("파일 이름을 확인할 수 없습니다.");
        }

        String fileExt = checkFileExtension(file.getOriginalFilename());
        if (!validImageExtension(fileExt)){
            throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다.");
        }

        Optional<Album> res = albumRepository.findById(albumId);

        if (res.isEmpty()) {
            throw new EntityNotFoundException("앨범이 존재하지 않습니다");
        }

        String fileName = file.getOriginalFilename();
        int fileSize = (int) file.getSize();
        fileName = checkFileName(fileName, albumId);
        saveFile(file, albumId, fileName);

        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(res.get());
        photoRepository.save(photo);

        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setFileSize((int) photo.getFileSize());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setThumbUrl(photo.getThumbUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setAlbumId(albumId);

        return photoDto;

    }

    private String checkFileName(String fileName, Long albumId) {
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        Optional<Photo> result = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);

        int count = 2;
        while (result.isPresent()) {
            fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
            result = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
            count++;
        }
        return fileName;
    }

    private String checkFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1){
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private boolean validImageExtension(String extension){
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
    }

    private void saveFile(MultipartFile file, Long albumId, String fileName) {
        try {
            String filePath = albumId + "/" + fileName;
            String original_path = Constants.PATH_PREFIX + "/photos/original";
            Path originalPath = Paths.get(original_path + "/" + albumId);
            String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";
            Path thumbPath = Paths.get(thumb_path + "/" + albumId);
            Files.createDirectories(originalPath);
            Files.createDirectories(thumbPath);

            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            ImageIO.write(thumbImg, Objects.requireNonNull(ext), thumbFile);

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error : " + e.getMessage());
        }
    }

    public PhotoDto getPhotoInfo(Long photoId) {
        Optional<Photo> result = photoRepository.findById(photoId);

        if (result.isPresent()) {
            Photo photo = result.get();
            PhotoDto response = new PhotoDto();
            response.setPhotoId(photo.getPhotoId());
            response.setAlbumId(photo.getAlbum().getAlbumId());
            response.setFileName(photo.getFileName());
            response.setOriginalUrl(photo.getOriginalUrl());
            response.setThumbUrl(photo.getThumbUrl());
            response.setUploadedAt(photo.getUploadedAt());
            response.setFileSize((int) photo.getFileSize());

            return response;
        } else {
            throw new RuntimeException("조회되는 사진이 없습니다.");
        }

    }

    public PhotosResponse deletePhoto(Long photoId) {
        Optional<Photo> photoOptional = photoRepository.findById(photoId);

        if (photoOptional.isEmpty()) {
            throw new RuntimeException("해당하는 사진이 없습니다.");
        }

        Photo photo = photoOptional.get();

        String albumId = String.valueOf(photo.getAlbum().getAlbumId());
        String fileName = photo.getFileName();
        String originFilePath = Constants.PATH_PREFIX + "/photos/original/" + albumId + "/" + fileName;
        String thumbFilePath = Constants.PATH_PREFIX + "/photos/thumb/" + albumId + "/" + fileName;

        try {
            Files.deleteIfExists(Paths.get(originFilePath));
            Files.deleteIfExists(Paths.get(thumbFilePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }

        PhotosResponse response = new PhotosResponse();
        response.setPhotoId(photo.getPhotoId());
        response.setFileName(photo.getFileName());
        response.setThumbUrl(photo.getThumbUrl());
        response.setUploadedAt(photo.getUploadedAt());

        photoRepository.delete(photo);

        return response;
    }

    public List<PhotosResponse> movePhotos(MovePhotosRequest request) {
        List<PhotosResponse> responses = new ArrayList<>();

        Long fromAlbumId = request.getFromAlbumId();
        Long toAlbumId = request.getToAlbumId();

        for (Long photoId : request.getPhotoIds()) {
            Optional<Photo> photoOptional = photoRepository.findById(photoId);

            if (photoOptional.isPresent()) {
                Photo photo = photoOptional.get();

                Album toAlbum = albumRepository.findById(toAlbumId).orElseThrow(() ->
                        new EntityNotFoundException("사진을 옮기려는 앨범이 존재하지 않습니다"));

                moveFile(fromAlbumId, toAlbumId, photo.getFileName());

                photo.setAlbum(toAlbum);
                photoRepository.save(photo);

                PhotosResponse photosResponse = new PhotosResponse();
                photosResponse.setPhotoId(photoId);
                photosResponse.setFileName(photo.getFileName());
                photosResponse.setThumbUrl(photo.getThumbUrl());
                photosResponse.setUploadedAt(photo.getUploadedAt());

                responses.add(photosResponse);

                Album fromAlbum = photo.getAlbum();
                fromAlbum.getPhotos().remove(photo);

            } else {
                throw new EntityNotFoundException("원본 앨범이 존재하지 않습니다.");
            }
        }

        return responses;
    }

    private void moveFile(Long fromAlbumId, Long toAlbumId, String fileName) {
        try {
            String fromOriginalFilePath = Constants.PATH_PREFIX + "/photos/original/" + fromAlbumId + "/" + fileName;
            String fromThumbFilePath = Constants.PATH_PREFIX + "/photos/thumb/" + fromAlbumId + "/" + fileName;

            String toOriginalFilePath = Constants.PATH_PREFIX + "/photos/original/" + toAlbumId + "/" + fileName;
            String toThumbFilePath = Constants.PATH_PREFIX + "/photos/thumb/" + toAlbumId + "/" + fileName;

            Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + toAlbumId));
            Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + toAlbumId));

            Files.move(Paths.get(fromOriginalFilePath), Paths.get(toOriginalFilePath), StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get(fromThumbFilePath), Paths.get(toThumbFilePath), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("파일 이동 중 오류가 발생했습니다. : " + e.getMessage());
        }
    }

    public List<PhotosResponse> getPhotoList(String keyword, String sort, Long albumId) {
        Optional<Album> albumOptional = albumRepository.findById(albumId);
        if (albumOptional.isEmpty()) {
            throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        }
        List<Photo> photos;
        if (Objects.equals(sort, "byName")) {
            photos = photoRepository.findByFileNameContainingAndAlbum_AlbumIdOrderByFileNameAsc(keyword, albumId);
        } else if (Objects.equals(sort, "byDate")) {
            photos = photoRepository.findByFileNameContainingAndAlbum_AlbumIdOrderByUploadedAt(keyword, albumId);
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }

        List<PhotosResponse> responses = new ArrayList<>();
        for (Photo photo : photos) {
            PhotosResponse res = new PhotosResponse();
            res.setPhotoId(photo.getPhotoId());
            res.setFileName(photo.getFileName());
            res.setThumbUrl(photo.getThumbUrl());
            res.setUploadedAt(photo.getUploadedAt());

            responses.add(res);
        }

        return responses;
    }

    public File getImageFile(Long photoId,Long albumId) {
        Optional<Album> album = albumRepository.findById(albumId);
        Optional<Photo> photo = photoRepository.findById(photoId);

        if (photo.isEmpty()) {
            throw new EntityNotFoundException("사진 ID : %d 를 찾을 수 없습니다.");
        }
        if (album.isEmpty() || !album.get().getPhotos().contains(photo.get())){
            throw new EntityNotFoundException("앨범 ID : %d 에 해당하는 앨범이 없거나, 해당하는 사진이 앨범에 속해있지 않습니다.");
        }
        return new File(Constants.PATH_PREFIX + photo.get().getOriginalUrl());
    }

    public File getImageFilesWithZip(Long[] photoIds,Long albumId) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            for (Long photoId : photoIds) {
                File file = getImageFile(photoId,albumId);
                FileInputStream fileInputStream = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fileInputStream.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }

                fileInputStream.close();
                zipOutputStream.closeEntry();
            }

            zipOutputStream.close();

            File zipFile = new File("photos.zip");
            try (FileOutputStream fos = new FileOutputStream(zipFile)) {
                byteArrayOutputStream.writeTo(fos);
            }

            return zipFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
