package com.devtino.livesync.domain.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleRequestDto {

    @NotBlank(message = "방송 제목은 필수입니다.")
    private String title;

    @NotNull(message = "방송 시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    @NotNull(message = "방송 종료 시간은 필수입니다.")
    private LocalDateTime endTime;

    private String description; // 선택 입력

    private List<Long> hostIds; // 배정할 쇼호스트 id 목록
}
