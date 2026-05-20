package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 조회", description = "로그인한 사용자의 계정 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<Memberdto.MyPageResponse> getMyPage(@AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(myPageService.getMyPage(memberId));
    }

    @Operation(summary = "프로필 수정", description = "이름과 소속만 수정합니다. 이메일·전화번호·알림설정은 변경되지 않습니다.")
    @PatchMapping("/profile")
    public ResponseEntity<Memberdto.MyPageResponse> updateProfile(
            @AuthenticationPrincipal Long memberId,
            @RequestBody Memberdto.ProfileUpdateRequest request) {
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(myPageService.updateProfile(memberId, request));
    }
}