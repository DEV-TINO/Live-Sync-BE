package com.devtino.livesync.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

/*
 * 쇼호스트 목록 조회 응답 DTO
 * - 관리자 화면 dropdown에서 사용
 */
@Getter
@Builder
public class ShowhostResponseDto {

    private Long id;          // 쇼호스트 ID
    private String loginId;   // 로그인 아이디
    private String nickname;  // 이름
}