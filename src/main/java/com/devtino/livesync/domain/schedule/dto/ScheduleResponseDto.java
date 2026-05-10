package com.devtino.livesync.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ScheduleResponseDto {

    private Long id;
    private String title;
    private String color;
    private String location;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ShowhostDto> showhosts;
    private List<FileDto> files;
}