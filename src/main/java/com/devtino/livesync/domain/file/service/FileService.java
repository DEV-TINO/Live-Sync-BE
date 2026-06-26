package com.devtino.livesync.domain.file.service;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.file.dto.FileDownload;
import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.repository.FileRepository;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.global.common.exception.CustomException;
import com.devtino.livesync.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    public List<String> uploadFiles(List<MultipartFile> files, Long memberId) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_EMPTY);
        }

        List<String> fileUrls = new ArrayList<>();

        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();

            if (file.isEmpty() || originalName == null || originalName.isBlank()) {
                throw new CustomException(ErrorCode.FILE_EMPTY);
            }

            try {
                String ext = "";
                int index = originalName.lastIndexOf(".");
                if (index != -1) {
                    ext = originalName.substring(index);
                }

                String savedName = UUID.randomUUID() + ext;

                File dest = new File(dir, savedName);
                file.transferTo(dest);

                FileEntity entity = FileEntity.builder()
                        .fileName(originalName)
                        .fileKey(savedName)
                        .member(member)
                        .isPublic(true) // 쇼호스트에게 공개
                        .build();

                FileEntity savedFile = fileRepository.save(entity);
                String fileUrl = "/files/download/" + savedFile.getId();
                savedFile.setFileUrl(fileUrl);
                fileRepository.save(savedFile);
                fileUrls.add(fileUrl);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return fileUrls;
    }

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
