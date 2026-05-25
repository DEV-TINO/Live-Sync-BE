package com.devtino.livesync.domain.schedule.dto;

import com.devtino.livesync.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

/*
 * 일정 응답에서 쇼호스트 정보 전달용 DTO
 */
@Getter
@Builder
public class ShowhostDto {

    private Long id;

    /*
     * 프론트에 표시될 이름 (닉네임)
     */
    private String name;

    public static ShowhostDto from(Member member) {
        return ShowhostDto.builder()
                .id(member.getId())
                .name(member.getNickname())
                .build();
    }
}