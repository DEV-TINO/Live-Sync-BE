package com.devtino.livesync.domain.file.service;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.file.dto.FileDownload;
import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.repository.FileRepository;
import com.devtino.livesync.global.common.exception.CustomException;
import com.devtino.livesync.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    public List<FileResponseDto> getFileList() {
        List<FileEntity> entities = fileRepository.findAll();
        List<FileResponseDto> result = new ArrayList<>();

        for (FileEntity entity : entities) {
            result.add(
                    FileResponseDto.builder()
                            .id(entity.getId())
                            .fileName(entity.getFileName())
                            .fileUrl(entity.getFileUrl())
                            .build()
            );
        }

        return result;
    }

    public List<FileResponseDto> getPublicFiles() {
        List<FileEntity> entities = fileRepository.findByIsPublicTrue();
        List<FileResponseDto> result = new ArrayList<>();

        for (FileEntity entity : entities) {
            result.add(
                    FileResponseDto.builder()
                            .id(entity.getId())
                            .fileName(entity.getFileName())
                            .fileUrl(entity.getFileUrl())
                            .build()
            );
        }

        return result;
    }

    public FileEntity getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
    }

    public FileDownload downloadFile(Long id) {
        FileEntity file = getFile(id);
        Path path = Paths.get(uploadDir, file.getFileKey()).normalize();

        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }

            return new FileDownload(file.getFileName(), resource);
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    public void deleteFile(Long id) {
        FileEntity file = getFile(id);

        File physicalFile = new File(uploadDir, file.getFileKey());

        if (physicalFile.exists() && !physicalFile.delete()) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }

        fileRepository.delete(file);
    }
}
