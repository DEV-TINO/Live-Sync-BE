package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /*
     * 쇼호스트 목록 조회
     */
    @Operation(summary = "쇼호스트 목록 조회")
    @GetMapping("/showhosts")
    public ResponseEntity<List<Memberdto.ShowhostResponse>> getShowhosts(
            @RequestParam Long workspaceId
    ) {
        return ResponseEntity.ok(
                memberService.getShowhosts(workspaceId)
        );
    }
}