package com.devtino.livesync.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String title;     // 제목
    private String content;   // 상세 내용

    private boolean isRead;   // 읽음 여부

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 타입 (일정/파일/정산)

    private String url; // 클릭 시 이동 URL

    private LocalDateTime createdAt;
}