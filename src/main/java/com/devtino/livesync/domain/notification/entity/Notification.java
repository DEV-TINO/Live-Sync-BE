package com.devtino.livesync.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.workspace.entity.Workspace;

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

    // 어떤 유저에게 가는 알림인지
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떤 workspace에서 발생한 알림인지 (핵심)
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    private String title;     // 제목
    private String content;   // 상세 내용

    private boolean isRead;   // 읽음 여부

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 타입 (일정/파일/정산)

    private String url; // 클릭 시 이동 URL

    private LocalDateTime createdAt;
}