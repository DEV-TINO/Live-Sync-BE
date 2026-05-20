package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.entity.MemberRole;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    /** 마이페이지 정보 조회 */
    @Transactional(readOnly = true)
    public Memberdto.MyPageResponse getMyPage(Long memberId)
    {
        Member member = findMember(memberId);
        return toResponse(member);
    }

    /** 프로필 수정 - 이름과 소속만 반영 */
    @Transactional
    public Memberdto.MyPageResponse updateProfile(Long memberId, Memberdto.ProfileUpdateRequest request)
    {
        // 이름이 비어버리면 화면 표시가 깨지므로 최소한의 검증
        if (request.getNickname() == null || request.getNickname().isBlank())
        {
            throw new RuntimeException("이름은 비워둘 수 없습니다.");
        }

        Member member = findMember(memberId);

        // updateProfile은 nickname/affiliation만 바꿀 수 있음 → 이메일·전화번호는 손댈 수 없음
        member.updateProfile(request.getNickname(), request.getAffiliation());

        // @Transactional 안이므로 변경 감지로 자동 UPDATE 됨 (save() 호출 불필요)
        return toResponse(member);
    }

    private Member findMember(Long memberId)
    {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
    }

    private Memberdto.MyPageResponse toResponse(Member member)
    {
        return Memberdto.MyPageResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .affiliation(member.getAffiliation())
                .role(member.getRole() != null ? member.getRole().name() : null)
                .roleName(toRoleName(member.getRole()))
                .build();
    }

    private String toRoleName(MemberRole role)
    {
        if (role == null) return "";
        switch (role.name())
        {
            case "ROLE_ADMIN":    return "전체 관리";
            case "ROLE_SHOWHOST": return "쇼호스트";
            case "ROLE_USER":     return "일반 회원";
            default:              return role.name();
        }
    }
}