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

    // 일반 로그인용 ID (소셜 가입 시에는 null 가능)
    @Column(unique = true)
    private String loginId;

    // 일반 로그인용 비밀번호 (소셜 가입 시에는 null 가능)
    private String password;

    private String nickname;

    // 마이페이지 - 소속
    private String affiliation;

    // 가입 경로 구분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    // 소셜 로그인 고유 식별자 (일반 가입 시에는 null 가능)
    @Column(unique = true, nullable = true)
    private String kakaoId;

    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    private MemberRole role;

    @Column
    private String refreshToken; // 추가

    // Refresh Token을 업데이트하거나 비우는(로그아웃) 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 프로필 업데이트 (기존 로직 유지)
    public Member update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public void updateProfile(String nickname, String affiliaiton)
    {
        this.nickname = nickname;
        this.affiliation = affiliaiton;
    }


    public void updatePassword(String password) {
        this.password = password;
    }
}