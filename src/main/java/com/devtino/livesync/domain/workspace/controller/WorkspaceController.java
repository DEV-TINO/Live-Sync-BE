package com.devtino.livesync.domain.workspace.controller;

import com.devtino.livesync.domain.workspace.entity.Workspace;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.service.WorkspaceService;
import com.devtino.livesync.global.config.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workspace", description = "워크스페이스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "워크스페이스 생성")
    @PostMapping("/create")
    public Workspace create(
            @RequestParam String token,
            @RequestParam String name
    ) {
        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));
        return workspaceService.createWorkspace(memberId, name);
    }

    @Operation(summary = "워크스페이스 초대")
    @PostMapping("/invite")
    public ResponseEntity<Long> invite(
            @RequestParam String token,
            @RequestParam Long workspaceId,
            @RequestParam String loginId
    ) {
        jwtTokenProvider.getMemberId(token); // 요청자 검증용
        Long inviteId = workspaceService.invite(workspaceId, loginId);
        return ResponseEntity.ok(inviteId);
    }

    @Operation(summary = "초대 수락")
    @PostMapping("/invite/{inviteId}/accept")
    public ResponseEntity<String> acceptInvite(
            @RequestParam String token,
            @PathVariable Long inviteId
    ) {
        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        workspaceService.acceptInvite(inviteId, memberId);

        return ResponseEntity.ok("초대 수락 완료");
    }

    @Operation(summary = "내 워크스페이스 조회")
    @GetMapping("/my")
    public List<MemberWorkspace> myWorkspaces(@RequestParam String token) {

        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        return workspaceService.getMyWorkspaces(memberId);
    }

    @Operation(summary = "워크스페이스 선택")
    @PostMapping("/select")
    public String selectWorkspace(
            @RequestParam String token,
            @RequestParam Long workspaceId
    ) {
        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        return workspaceService.selectWorkspace(memberId, workspaceId);
    }
}