package com.devtino.livesync.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Schema(description = "일정 등록 요청 (multipart)")
public class ScheduleMultipartRequest {

    @Schema(description = "일정 정보 (JSON)")
    private ScheduleRequestDto schedule;

    @Schema(description = "첨부 파일 (선택)")
    private List<MultipartFile> files;
}