package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.ShowhostInviteRequest;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.entity.MemberRole;
import com.devtino.livesync.domain.member.entity.LoginType;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devtino.livesync.domain.member.dto.ShowhostResponseDto;
import java.util.List;

 // 쇼호스트 초대 및 관리 서비스
@Service
@RequiredArgsConstructor
public class ShowhostService {

    private final MemberRepository memberRepository;

     // 쇼호스트 초대
    @Transactional
    public void inviteShowhost(ShowhostInviteRequest request) {

        // 중복 체크
        if (memberRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .nickname(request.getNickname())
                .password(null) // 아직 비밀번호 없음
                .loginType(LoginType.GENERAL)
                .role(MemberRole.ROLE_SHOWHOST)
                .build();

        memberRepository.save(member);
    }

     // 쇼호스트 목록 조회 - 관리자 화면
    public List<ShowhostResponseDto> getShowhosts() {

        return memberRepository.findAll().stream()
                .filter(member -> member.getRole().name().equals("ROLE_SHOWHOST"))
                .map(member -> ShowhostResponseDto.builder()
                        .id(member.getId())
                        .loginId(member.getLoginId())
                        .nickname(member.getNickname())
                        .build()
                )
                .toList();
    }
}