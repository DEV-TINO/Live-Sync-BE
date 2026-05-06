package com.devtino.livesync.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Memberdto {
    // 회원가입 요청
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest {
        private String loginId;
        private String password;
        private String nickname;
    }

    // 로그인 요청
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String loginId;
        private String password;
    }

    // 응답용 (보안상 비밀번호는 제외)
    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberResponse {
        private Long id;
        private String loginId;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JwtTokenResponse{
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReissueRequest {
        private String refreshToken;
    }
}
