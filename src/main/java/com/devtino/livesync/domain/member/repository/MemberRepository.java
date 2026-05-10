package com.devtino.livesync.domain.member.repository;

import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이미 가입된 유저인지 확인하기 위해 카카오 ID로 조회
    Optional<Member> findByKakaoId(String kakaoId);

    // 일반 로그인용 ID 조회
    Optional<Member> findByLoginId(String loginId);

    // Refresh Token으로 유저 조회
    Optional<Member> findByRefreshToken(String refreshToken);

    /*
     * 쇼호스트 목록 조회
     * - ROLE_SHOWHOST인 사용자만 조회
     * - 프론트에서 dropdown 리스트로 사용
     */
    List<Member> findByRole(MemberRole role);
}