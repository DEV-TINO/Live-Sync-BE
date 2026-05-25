package com.devtino.livesync.domain.schedule.domain;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

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
    private String location;     // ex. 스튜드오 c
    private String address;      // ex. 서울 성동구 성수이로 113
    private Double latitude;     // ex. 37.5447(위도)
    private Double longitude;    // ex. 127.0557(경도)
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /*
     * 쇼호스트 여러 명 배정
     */
    @ManyToMany
    @JoinTable(
            name = "schedule_member",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> showhosts;

    /*
     * 일정에 연결된 파일들
     */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}