package com.devtino.livesync.domain.schedule.dto;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.schedule.entity.Schedule;
import com.devtino.livesync.domain.schedule.entity.ScheduleAssignment;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String status;
    private List<HostDto> hosts;
    private List<FileDto> files;

    @Getter
    @Builder
    public static class HostDto {
        private Long memberId;
        private String nickname;
        private Long appliedPay;

        public static HostDto from(ScheduleAssignment assignment) {
            return HostDto.builder()
                    .memberId(assignment.getMember().getId())
                    .nickname(assignment.getMember().getNickname())
                    .appliedPay(assignment.getAppliedPay())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FileDto {
        private Long fileId;
        private String fileName;
        private String fileUrl;
        private int version;
        private boolean isLatest;

        public static FileDto from(FileEntity file) {
            return FileDto.builder()
                    .fileId(file.getId())
                    .fileName(file.getFileName())
                    .fileUrl(file.getFileUrl())
                    .version(file.getVersion())
                    .isLatest(file.isLatest())
                    .build();
        }
    }

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .description(schedule.getDescription())
                .status(schedule.getStatus().name())
                .hosts(schedule.getAssignments().stream()
                        .map(HostDto::from)
                        .collect(Collectors.toList()))
                .files(schedule.getFiles().stream()
                        .filter(FileEntity::isLatest)
                        .map(FileDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}