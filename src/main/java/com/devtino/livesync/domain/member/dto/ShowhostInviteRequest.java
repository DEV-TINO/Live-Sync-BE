package com.devtino.livesync.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
 * 관리자 → 쇼호스트 초대 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowhostInviteRequest {

    private String loginId;
    private String nickname;
}