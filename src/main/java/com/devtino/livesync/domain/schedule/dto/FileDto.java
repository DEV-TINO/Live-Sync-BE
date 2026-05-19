package com.devtino.livesync.domain.schedule.dto;

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
}