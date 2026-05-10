package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.ShowhostInviteRequest;
import com.devtino.livesync.domain.member.service.ShowhostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.devtino.livesync.domain.member.dto.ShowhostResponseDto;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/*
 * 관리자 전용 API
 */
@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ShowhostService showhostService;

    /*
     * 쇼호스트 초대 API
     */
    @Operation(summary = "쇼호스트 초대")
    @PostMapping("/showhost/invite")
    public ResponseEntity<String> inviteShowhost(@RequestBody ShowhostInviteRequest request) {

        showhostService.inviteShowhost(request);

        return ResponseEntity.ok("쇼호스트 초대 완료");
    }
    /*
     * 쇼호스트 목록 조회
     */
    @Operation(summary = "쇼호스트 목록 조회")
    @GetMapping("/showhosts")
    public ResponseEntity<List<ShowhostResponseDto>> getShowhosts() {

        return ResponseEntity.ok(
                showhostService.getShowhosts()
        );
    }
}