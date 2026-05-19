package com.devtino.livesync.domain.notification.repository;

import com.devtino.livesync.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 목록 조회 (최신순)
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // unread count 조회
    long countByMemberIdAndIsReadFalse(Long memberId);
}