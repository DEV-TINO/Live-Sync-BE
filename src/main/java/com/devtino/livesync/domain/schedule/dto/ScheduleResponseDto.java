package com.devtino.livesync.domain.schedule.dto;

import com.devtino.livesync.domain.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ScheduleResponseDto {

    private Long id;
    private String title;
    private String color;
    private String location;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ShowhostDto> showhosts;
    private List<FileDto> files;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .color(schedule.getColor())
                .location(schedule.getLocation())
                .address(schedule.getAddress())
                .latitude(schedule.getLatitude())
                .longitude(schedule.getLongitude())
                .description(schedule.getDescription())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .showhosts(schedule.getShowhosts().stream()
                        .map(ShowhostDto::from)
                        .collect(Collectors.toList()))
                .files(schedule.getFiles().stream()
                        .map(FileDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}