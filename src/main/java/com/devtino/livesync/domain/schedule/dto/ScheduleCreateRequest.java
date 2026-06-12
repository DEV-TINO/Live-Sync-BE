package com.devtino.livesync.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleCreateRequest {

    private String title;
    private String color;
    private String location;
    private String address;
    private Double latitude;   // 위도
    private Double longitude;  // 경도
    private String description;
    private Long workspaceId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private List<String> showhostLoginIds;
}