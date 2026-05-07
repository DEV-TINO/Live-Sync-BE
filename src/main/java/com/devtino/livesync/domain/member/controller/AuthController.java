package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 및 인가 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Memberdto.SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<Memberdto.JwtTokenResponse> login(@RequestBody Memberdto.LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 세션을 종료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }
        authService.logout(memberId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 Access Token을 재발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<Memberdto.JwtTokenResponse> reissue(@RequestBody Memberdto.ReissueRequest reissueRequest) {
        // 클라이언트가 보낸 RT로 새로운 토큰 세트를 내려줍니다.
        return ResponseEntity.ok(authService.reissue(reissueRequest.getRefreshToken()));
    }

}
