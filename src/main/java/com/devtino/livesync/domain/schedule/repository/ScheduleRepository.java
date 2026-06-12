package com.devtino.livesync.domain.schedule.repository;

import com.devtino.livesync.domain.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /*
     * 쇼호스트 기준 일정 조회
     */
    List<Schedule> findByShowhosts_Id(Long memberId);

    /*
     * 워크스페이스 기준 전체 일정 조회
     */
    List<Schedule> findByWorkspace_Id(Long workspaceId);

    /*
     * 여러 워크스페이스 조회 (전체 목록용)
     */
    List<Schedule> findByWorkspace_IdIn(List<Long> workspaceIds);
}