package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private Long workspaceId;
    private Long memberId;
    private String title;
    private String content;
    private NotificationType type;
    private String url;
}