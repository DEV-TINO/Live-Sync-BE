package com.devtino.livesync.domain.file.dto;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.schedule.dto.FileDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDto {

    /*
     * 파일 ID (DB PK)
     */
    private Long id;

    /*
     * 원본 파일명
     */
    private String fileName;

    /*
     * 다운로드 URL
     */
    private String fileUrl;

    public static FileDto from(FileEntity file) {
        return FileDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .build();
    }
}