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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Memberdto.SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<Memberdto.JwtTokenResponse> login(@RequestBody Memberdto.LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }
        authService.logout(memberId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Memberdto.JwtTokenResponse> reissue(@RequestBody Memberdto.ReissueRequest reissueRequest) {
        // 클라이언트가 보낸 RT로 새로운 토큰 세트를 내려줍니다.
        return ResponseEntity.ok(authService.reissue(reissueRequest.getRefreshToken()));
    }

}
