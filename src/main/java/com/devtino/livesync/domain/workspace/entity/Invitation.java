package com.devtino.livesync.domain.workspace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 초대받은 워크스페이스
    private Long workspaceId;

    // 초대받은 유저
    private Long memberId;

    // 상태
    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    private LocalDateTime createdAt;

    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public void accept() {
        this.status = InviteStatus.ACCEPTED;
    }

    public void reject() {
        this.status = InviteStatus.REJECTED;
    }
}