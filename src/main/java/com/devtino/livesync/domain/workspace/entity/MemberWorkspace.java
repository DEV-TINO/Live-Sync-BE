package com.devtino.livesync.domain.workspace.entity;

import com.devtino.livesync.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /*
     * 워크스페이스
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    /*
     * 권한
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN,
        SHOWHOST
    }
}