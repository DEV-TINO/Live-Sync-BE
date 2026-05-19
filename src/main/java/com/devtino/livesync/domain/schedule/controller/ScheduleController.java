package com.devtino.livesync.domain.schedule.controller;

import com.devtino.livesync.domain.schedule.dto.ScheduleMultipartRequest;
import com.devtino.livesync.domain.schedule.dto.ScheduleRequestDto;
import com.devtino.livesync.domain.schedule.dto.ScheduleResponseDto;
import com.devtino.livesync.domain.schedule.service.ScheduleService;
import com.devtino.livesync.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "일정 관리 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일정 등록", description = "방송 일정을 등록합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ScheduleMultipartRequest.class),
                    encoding = @Encoding(name = "schedule", contentType = "application/json")
            )
    )
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> createSchedule(
            @Parameter(hidden = true)
            @RequestPart("schedule") @Valid ScheduleRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Long memberId
    ) {
        ScheduleResponseDto response = scheduleService.createSchedule(requestDto, files, memberId);
        return ResponseEntity.ok(ApiResponse.success("일정이 등록되었습니다.", response));
    }

    // 월별 일정 조회
    @GetMapping
    @Operation(summary = "월별 일정 조회", description = "year, month로 일정 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getSchedules(
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<ScheduleResponseDto> response = scheduleService.getSchedulesByMonth(year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 일정 상세 조회
    @GetMapping("/{id}")  // ← /schedules 중복 제거
    @Operation(summary = "일정 상세 조회", description = "일정 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> getSchedule(
            @PathVariable Long id
    ) {
        ScheduleResponseDto response = scheduleService.getSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 일정 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // ← /schedules 중복 제거
    @Operation(summary = "일정 수정", description = "일정 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ScheduleMultipartRequest.class),
                    encoding = @Encoding(name = "schedule", contentType = "application/json")
            )
    )
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> updateSchedule(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestPart("schedule") @Valid ScheduleRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Long memberId
    ) {
        ScheduleResponseDto response = scheduleService.updateSchedule(id, requestDto, files, memberId);
        return ResponseEntity.ok(ApiResponse.success("일정이 수정되었습니다.", response));
    }

    // 일정 삭제
    @DeleteMapping("/{id}")  // ← /schedules 중복 제거
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success("일정이 삭제되었습니다.", null));
    }
}