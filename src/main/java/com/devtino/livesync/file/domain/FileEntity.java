package com.devtino.livesync.file.domain;

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
     * 업로드한 사용자 ID
     * - 로그인 기능 연동 시 사용
     * - 현재는 임시값으로 저장 가능
     * - 나중에 JWT에서 userId 추출해서 저장
     */
    private Long userId;
}