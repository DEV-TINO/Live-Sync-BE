package com.devtino.livesync.domain.workspace.repository;

import com.devtino.livesync.domain.workspace.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByMemberId(Long memberId);
}