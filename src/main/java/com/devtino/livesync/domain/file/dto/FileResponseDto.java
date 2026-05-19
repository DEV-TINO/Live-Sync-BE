package com.devtino.livesync.domain.file.dto;

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

    private int version;
    private boolean isLatest;
}