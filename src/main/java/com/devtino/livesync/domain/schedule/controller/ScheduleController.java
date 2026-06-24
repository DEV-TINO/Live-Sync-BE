package com.devtino.livesync.domain.schedule.controller;

import com.devtino.livesync.domain.schedule.dto.ScheduleCreateRequest;
import com.devtino.livesync.domain.schedule.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Schedules", description = "스케줄 관련 API")
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ObjectMapper objectMapper;

    /*
     * 일정 생성 (관리자)
     */
    @Operation(summary = "일정 생성 (관리자)")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> createSchedule(
            @AuthenticationPrincipal Long memberId,
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {

        ScheduleCreateRequest request =
                objectMapper.readValue(data, ScheduleCreateRequest.class);

        scheduleService.createSchedule(memberId, request, files);

        return ResponseEntity.ok("일정 생성 완료");
    }

    /*
     * 전체 일정 조회 (관리자) - workspace 기준
     */
    @Operation(summary = "전체 일정 조회 (관리자)")
    @GetMapping
    public ResponseEntity<?> getAllSchedules(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Long workspaceId
    ) {
        return ResponseEntity.ok(
                scheduleService.getAllSchedules(memberId, workspaceId)
        );
    }

    /*
     * 내 일정 조회 (쇼호스트)
     */
    @Operation(summary = "내 일정 조회 (쇼호스트)")
    @GetMapping("/my")
    public ResponseEntity<?> getMySchedules(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Long workspaceId
    ) {
        return ResponseEntity.ok(
                scheduleService.getMySchedules(memberId, workspaceId)
        );
    }

    /*
     * 일정 상세 조회 (관리자)
     */
    @Operation(summary = "일정 상세 조회 (관리자)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSchedule(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                scheduleService.getSchedule(memberId, id)
        );
    }

    /*
     * 일정 수정
     */
    @Operation(summary = "일정 수정")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSchedule(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long id,
            @RequestBody ScheduleCreateRequest request
    ) {

        scheduleService.updateSchedule(memberId, id, request);

        return ResponseEntity.ok("일정 수정 완료");
    }

    /*
     * 일정 삭제
     */
    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long id
    ) {

        scheduleService.deleteSchedule(memberId, id);

        return ResponseEntity.ok("일정 삭제 완료");
    }
}
