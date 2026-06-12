package com.devtino.livesync.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 아이디 (일반 로그인용)
    @Column(unique = true)
    private String loginId;

    // 비밀번호 (일반 로그인용, 소셜 로그인 시 null 가능)
    private String password;

    // 사용자 닉네임
    private String nickname;

    // 소속 정보 (회사/기관 등)
    private String affiliation;

    // 로그인 방식 구분 (LOCAL / KAKAO 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    // 카카오 로그인 시 사용되는 고유 식별자
    // 일반 로그인 사용자는 null
    @Column(unique = true, nullable = true)
    private String kakaoId;

    // 리프레시 토큰 (재로그인 없이 access token 재발급용)
    @Column
    private String refreshToken;

    /*
     * 역할 필드 제거 이유
     *
     * 기존에는 Member 단위로 ADMIN / SHOWHOST를 구분하려 했으나,
     * 현재 구조에서는 워크스페이스 단위 권한(MemberWorkspace.role)이
     * 실제 권한을 결정하므로 Member.role은 불필요함.
     *
     * → 권한 구조:
     *   Member = 계정 정보
     *   MemberWorkspace = 권한 (ADMIN / SHOWHOST)
     */

    /*
     * 리프레시 토큰 업데이트
     * 로그인 유지 및 토큰 재발급을 위해 사용
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /*
     * 닉네임 변경
     */
    public Member update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    /*
     * 프로필 수정 (닉네임 + 소속)
     */
    public void updateProfile(String nickname, String affiliation) {
        this.nickname = nickname;
        this.affiliation = affiliation;
    }

    /*
     * 비밀번호 변경 (일반 로그인 사용자용)
     */
    public void updatePassword(String password) {
        this.password = password;
    }
}