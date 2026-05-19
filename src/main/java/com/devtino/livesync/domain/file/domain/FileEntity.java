package com.devtino.livesync.domain.file.domain;

import com.devtino.livesync.domain.schedule.entity.Schedule;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.schedule.domain.Schedule;
import jakarta.persistence.*;
import lombok.*;

/**
 * 파일 정보를 저장하는 Entity 클래스
 * DB의 file 테이블과 매핑
 */
@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {

    /**
     * 파일 고유 ID (Primary Key)
     * AUTO_INCREMENT 방식으로 생성됨
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 원본 파일 이름
     * 예: test.png
     */
    private String fileName;

    /**
     * 파일 접근 URL
     * 예: https://s3.amazonaws.com/... 또는 서버 URL
     */
    private String fileUrl;

    /**
     * 파일 저장 키 (S3 key 또는 내부 식별값)
     * 예: uploads/uuid.png
     */
    private String fileKey;

    /**
     * 업로드한 사용자 (관리자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 이 파일이 속한 일정
     * - 하나의 파일은 하나의 일정에 속함
     * - 일정 기준으로 파일 조회 가능
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    /**
     * 파일 공개 여부
     * - true  : 쇼호스트 조회 가능
     * - false : 관리자만 조회 가능
     */
    private boolean isPublic;

    // 일정 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // 버전 관리 v1 v2 v3
    @Column(nullable = false)
    @Builder.Default
    private int version = 1;

    // 최신 버전 여부
    @Column(nullable = false)
    @Builder.Default
    private boolean isLatest = true;

    // 구버전 처리 메서드
    public void markAsOldVersion() {
        this.isLatest = false;
    }
}