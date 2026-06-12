package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.repository.MemberWorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberWorkspaceRepository memberWorkspaceRepository;

    /*
     * 쇼호스트 목록 조회
     * - workspace 기준 SHOWHOST만 조회
     */
    public List<Memberdto.ShowhostResponse> getShowhosts(Long workspaceId) {

        List<MemberWorkspace> list =
                memberWorkspaceRepository.findByWorkspaceId(workspaceId);

        return list.stream()
                .filter(mw -> mw.getRole() == MemberWorkspace.Role.SHOWHOST)
                .map(mw -> {
                    Member m = mw.getMember();

                    return Memberdto.ShowhostResponse.builder()
                            .id(m.getId())
                            .nickname(m.getNickname())
                            .loginId(m.getLoginId())
                            .build();
                })
                .toList();
    }
}