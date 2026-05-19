package com.devtino.livesync.domain.schedule.repository;

import com.devtino.livesync.domain.schedule.entity.ScheduleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleAssignmentRepository extends JpaRepository<ScheduleAssignment, Long> {

    List<ScheduleAssignment> findByScheduleId(Long scheduleId);

    void deleteByScheduleId(Long scheduleId);
}
