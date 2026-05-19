package com.devtino.livesync.domain.schedule.controller;

import com.devtino.livesync.domain.schedule.dto.ScheduleCreateRequest;
import com.devtino.livesync.domain.schedule.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

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
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {

        // ObjectMapper 사용
        ScheduleCreateRequest request =
                objectMapper.readValue(data, ScheduleCreateRequest.class);

        scheduleService.createSchedule(request, files);

        return ResponseEntity.ok("일정 생성 완료");
    }

    /*
     * 관리자 → 전체 일정 조회
     */
    @Operation(summary = "전체 일정 조회 (관리자)")
    @GetMapping
    public ResponseEntity<?> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    /*
     * 쇼호스트 → 내 일정 조회
     */
    @Operation(summary = "내 일정 조회 (쇼호스트)")
    @GetMapping("/my")
    public ResponseEntity<?> getMySchedules(
            @AuthenticationPrincipal Long memberId
    ) {
        return ResponseEntity.ok(scheduleService.getMySchedules(memberId));
    }

    /*
     * 일정 상세 조회
     */
    @Operation(summary = "일정 상세 조회 (관리자)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }
}