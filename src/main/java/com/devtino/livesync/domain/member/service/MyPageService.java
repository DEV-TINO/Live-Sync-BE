package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.repository.MemberWorkspaceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final MemberWorkspaceRepository memberWorkspaceRepository;

    @Transactional(readOnly = true)
    public Memberdto.MyPageResponse getMyPage(Long memberId) {

        Member member = findMember(memberId);

        MemberWorkspace mw = memberWorkspaceRepository
                .findByMemberId(memberId)
                .stream()
                .findFirst()
                .orElse(null);

        String role = (mw != null) ? mw.getRole().name() : null;

        return toResponse(member, role);
    }

    @Transactional
    public Memberdto.MyPageResponse updateProfile(
            Long memberId,
            Memberdto.ProfileUpdateRequest request
    ) {

        if (request.getNickname() == null || request.getNickname().isBlank()) {
            throw new RuntimeException("이름은 비워둘 수 없습니다.");
        }

        Member member = findMember(memberId);
        member.updateProfile(request.getNickname(), request.getAffiliation());

        MemberWorkspace mw = memberWorkspaceRepository
                .findByMemberId(memberId)
                .stream()
                .findFirst()
                .orElse(null);

        String role = (mw != null) ? mw.getRole().name() : null;

        return toResponse(member, role);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
    }

    private Memberdto.MyPageResponse toResponse(Member member, String role) {

        return Memberdto.MyPageResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .affiliation(member.getAffiliation())
                .role(role)
                .roleName(toRoleName(role))
                .build();
    }

    private String toRoleName(String role) {

        if (role == null) return "";

        switch (role) {
            case "ADMIN":
                return "전체 관리";
            case "SHOWHOST":
                return "쇼호스트";
            default:
                return role;
        }
    }
}