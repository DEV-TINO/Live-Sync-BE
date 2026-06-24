package com.devtino.livesync.domain.workspace.service;

import java.time.LocalDateTime;
import java.util.List;

import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.entity.Workspace;
import com.devtino.livesync.domain.workspace.entity.Invitation;
import com.devtino.livesync.domain.workspace.repository.MemberWorkspaceRepository;
import com.devtino.livesync.domain.workspace.repository.WorkspaceRepository;
import com.devtino.livesync.domain.workspace.repository.InvitationRepository;
import com.devtino.livesync.global.config.JwtTokenProvider;

import com.devtino.livesync.global.sse.NotificationService;
import com.devtino.livesync.domain.notification.entity.NotificationType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberWorkspaceRepository memberWorkspaceRepository;
    private final InvitationRepository invitationRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NotificationService notificationService;

    // 워크스페이스 생성
    public Workspace createWorkspace(Long ownerId, String name) {

        Member owner = memberRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Workspace workspace = workspaceRepository.save(
                Workspace.builder()
                        .name(name)
                        .owner(owner)
                        .build()
        );

        memberWorkspaceRepository.save(
                MemberWorkspace.builder()
                        .member(owner)
                        .workspace(workspace)
                        .role(MemberWorkspace.Role.ADMIN)
                        .build()
        );

        return workspace;
    }

    // 초대 생성 (loginId 기반)
    public Long invite(Long requesterId, Long workspaceId, String loginId) {

        MemberWorkspace requesterWorkspace = memberWorkspaceRepository
                .findByMemberIdAndWorkspaceId(requesterId, workspaceId)
                .orElseThrow(() -> new RuntimeException("워크스페이스 접근 권한 없음"));

        if (requesterWorkspace.getRole() != MemberWorkspace.Role.ADMIN) {
            throw new RuntimeException("관리자 권한 필요");
        }

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        System.out.println("INVITE TARGET = " + member.getId());

        Invitation invite = invitationRepository.save(
                Invitation.builder()
                        .workspaceId(workspaceId)
                        .memberId(member.getId())
                        .status(Invitation.InviteStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        System.out.println("SEND NOTIFICATION TO = " + member.getId());

        notificationService.send(
                workspaceId,
                member.getId(),
                "워크스페이스 초대가 도착했습니다",
                "워크스페이스 초대",
                NotificationType.WORKSPACE_INVITE,
                "/workspace/invites"
        );

        return invite.getId();
    }

    // 초대 수락
    public void acceptInvite(Long inviteId, Long memberId) {

        Invitation invite = invitationRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("초대 없음"));

        if (!invite.getMemberId().equals(memberId)) {
            throw new RuntimeException("권한 없음");
        }

        invite.accept();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Workspace workspace = workspaceRepository.findById(invite.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("워크스페이스 없음"));

        memberWorkspaceRepository.save(
                MemberWorkspace.builder()
                        .member(member)
                        .workspace(workspace)
                        .role(MemberWorkspace.Role.SHOWHOST)
                        .build()
        );

        Member admin = workspace.getOwner();

        notificationService.send(
                workspace.getId(),
                admin.getId(),
                member.getNickname() + "님이 초대를 수락했습니다",
                "초대 수락",
                NotificationType.WORKSPACE_INVITE_ACCEPT,
                "/workspace/" + workspace.getId()
        );
    }

    public List<MemberWorkspace> getMyWorkspaces(Long memberId) {
        return memberWorkspaceRepository.findByMemberId(memberId);
    }

    public String selectWorkspace(Long memberId, Long workspaceId) {

        MemberWorkspace mw = memberWorkspaceRepository
                .findByMemberIdAndWorkspaceId(memberId, workspaceId)
                .orElseThrow(() -> new RuntimeException("접근 권한 없음"));

        return jwtTokenProvider.createToken(
                memberId,
                mw.getRole().name(),
                workspaceId
        );
    }

    public List<Invitation> getInvites(Long memberId) {
        return invitationRepository.findByMemberId(memberId);
    }
}
