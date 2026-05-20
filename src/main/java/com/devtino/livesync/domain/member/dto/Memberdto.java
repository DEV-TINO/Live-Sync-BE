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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowhostLoginRequest {
        private Long memberId;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShowhostResponse {
        private Long id;
        private String nickname;
        private String loginId;
    }

    // 마이페이지 조회 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyPageResponse {
        private Long id;
        private String loginId;     // 일반 로그인 아이디
        private String nickname;    // 화면의 "이름"
        private String affiliation; // 소속
        private String role;        // 권한 코드 (ROLE_ADMIN)
        private String roleName;    // 권한 표시명 (전체 관리)
    }

    // 프로필 수정 요청 - 이름(nickname)과 소속(affiliation)만 받음
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequest {
        private String nickname;
        private String affiliation;
    }
}
