package com.devtino.livesync.domain.workspace.repository;

import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface MemberWorkspaceRepository extends JpaRepository<MemberWorkspace, Long> {

    // 특정 유저가 속한 워크스페이스 목록 조회
    List<MemberWorkspace> findByMemberId(Long memberId);
    List<MemberWorkspace> findByWorkspaceId(Long workspaceId);
    Optional<MemberWorkspace> findByMemberIdAndWorkspaceId(Long memberId, Long workspaceId);
    boolean existsByMemberIdAndWorkspaceId(Long memberId, Long workspaceId);
}