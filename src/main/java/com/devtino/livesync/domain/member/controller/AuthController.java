package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.AuthService;
import com.devtino.livesync.domain.member.service.GeneralAuthService;
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
public class AuthController
{

    private final AuthService authService;
    private final GeneralAuthService generalAuthService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Memberdto.SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    @Operation(summary = "관리자 로그인")
    @PostMapping("/login")
    public ResponseEntity<Memberdto.JwtTokenResponse> login(
            @RequestBody Memberdto.LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    /*
     * 쇼호스트 로그인
     */
    @Operation(summary = "쇼호스트 로그인")
    @PostMapping("/showhost-login")
    public ResponseEntity<Memberdto.JwtTokenResponse> showhostLogin(
            @RequestBody Memberdto.ShowhostLoginRequest request) {

        return ResponseEntity.ok(
                generalAuthService.showhostLogin(request)
        );
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Long memberId) {

        authService.logout(memberId);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Memberdto.JwtTokenResponse> refresh(
            @RequestBody Memberdto.ReissueRequest request) {

        return ResponseEntity.ok(
                authService.reissue(request.getRefreshToken())
        );
    }
}