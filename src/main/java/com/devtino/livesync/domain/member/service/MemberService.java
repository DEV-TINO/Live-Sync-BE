package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.entity.MemberRole;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /*
     * 쇼호스트 목록 조회
     * - ROLE_SHOWHOST인 사용자만 반환
     */
    public List<Memberdto.ShowhostResponse> getShowhosts() {

        List<Member> members = memberRepository.findByRole(MemberRole.ROLE_SHOWHOST);

        List<Memberdto.ShowhostResponse> result = new ArrayList<>();

        for (Member m : members) {
            result.add(
                    Memberdto.ShowhostResponse.builder()
                            .id(m.getId())
                            .nickname(m.getNickname())
                            .loginId(m.getLoginId())
                            .build()
            );
        }

        return result;
    }
}