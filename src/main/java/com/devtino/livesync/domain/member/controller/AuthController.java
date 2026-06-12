package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 및 인가 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * 회원가입
     */
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody Memberdto.SignupRequest request
    ) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    /*
     * 로그인 (통합)
     * → role은 MemberWorkspace에서 판단
     */
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<Memberdto.JwtTokenResponse> login(
            @RequestBody Memberdto.LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /*
     * 로그아웃
     */
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal Long memberId
    ) {
        authService.logout(memberId);
        return ResponseEntity.ok("로그아웃 완료");
    }

    /*
     * 토큰 재발급
     */
    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<Memberdto.JwtTokenResponse> refresh(
            @RequestBody Memberdto.ReissueRequest request
    ) {
        return ResponseEntity.ok(
                authService.reissue(request.getRefreshToken())
        );
    }
}