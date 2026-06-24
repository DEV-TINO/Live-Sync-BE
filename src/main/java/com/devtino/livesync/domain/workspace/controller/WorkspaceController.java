package com.devtino.livesync.domain.workspace.controller;

import com.devtino.livesync.domain.workspace.entity.Workspace;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.service.WorkspaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workspace", description = "워크스페이스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "워크스페이스 생성")
    @PostMapping("/create")
    public Workspace create(
            @AuthenticationPrincipal Long memberId,
            @RequestParam String name
    ) {
        return workspaceService.createWorkspace(memberId, name);
    }

    @Operation(summary = "워크스페이스 초대")
    @PostMapping("/invite")
    public ResponseEntity<Long> invite(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Long workspaceId,
            @RequestParam String loginId
    ) {
        Long inviteId = workspaceService.invite(memberId, workspaceId, loginId);
        return ResponseEntity.ok(inviteId);
    }

    @Operation(summary = "초대 수락")
    @PostMapping("/invite/{inviteId}/accept")
    public ResponseEntity<String> acceptInvite(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inviteId
    ) {
        workspaceService.acceptInvite(inviteId, memberId);

        return ResponseEntity.ok("초대 수락 완료");
    }

    @Operation(summary = "내 워크스페이스 조회")
    @GetMapping("/my")
    public List<MemberWorkspace> myWorkspaces(@AuthenticationPrincipal Long memberId) {
        return workspaceService.getMyWorkspaces(memberId);
    }

    @Operation(summary = "워크스페이스 선택")
    @PostMapping("/select")
    public String selectWorkspace(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Long workspaceId
    ) {
        return workspaceService.selectWorkspace(memberId, workspaceId);
    }
}
