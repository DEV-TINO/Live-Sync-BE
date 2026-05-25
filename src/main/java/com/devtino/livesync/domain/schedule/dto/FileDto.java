package com.devtino.livesync.domain.schedule.dto;

import com.devtino.livesync.domain.file.domain.FileEntity;
import lombok.Builder;
import lombok.Getter;

/*
 * 일정 응답에서 파일 정보 전달용 DTO
 */
@Getter
@Builder
public class FileDto {

    private Long id;

    private String fileName;

    private String fileUrl;

    public static FileDto from(FileEntity file) {
        return FileDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .build();
    }
}