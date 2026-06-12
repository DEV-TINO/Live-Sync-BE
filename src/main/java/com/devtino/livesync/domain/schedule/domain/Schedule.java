package com.devtino.livesync.domain.schedule.domain;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import com.devtino.livesync.domain.workspace.entity.Workspace;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String color;
    private String location;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /*
     * 쇼호스트 배정
     */
    @ManyToMany
    @JoinTable(
            name = "schedule_member",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> showhosts;

    /*
     * 일정 파일
     */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files;

    /*
     * 워크스페이스 기준 소속
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    public void update(
            String title,
            String color,
            String location,
            String address,
            Double latitude,
            Double longitude,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.title = title;
        this.color = color;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}