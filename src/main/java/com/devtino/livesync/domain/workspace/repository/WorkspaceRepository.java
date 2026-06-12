package com.devtino.livesync.domain.workspace.repository;

import com.devtino.livesync.domain.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}