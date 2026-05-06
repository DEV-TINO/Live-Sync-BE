package com.devtino.livesync.domain.member.repository;

import com.devtino.livesync.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이미 가입된 유저인지 확인하기 위해 카카오 ID로 조회
    Optional<Member> findByKakaoId(String kakaoId);
    Optional<Member> findByLoginId(String loginId); // 일반 로그인
    Optional<Member> findByRefreshToken(String refreshToken);
}