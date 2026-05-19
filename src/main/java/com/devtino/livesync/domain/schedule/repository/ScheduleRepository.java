package com.devtino.livesync.domain.schedule.repository;

import com.devtino.livesync.domain.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 쇼호스트 기준 일정 조회
    List<Schedule> findByShowhosts_Id(Long memberId);
}
