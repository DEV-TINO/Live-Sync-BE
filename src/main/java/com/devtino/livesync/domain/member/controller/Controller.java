package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final MemberService memberService;

    @GetMapping("/home")
    public String home() {
        return "로그인 성공!";
    }

    /*
     * 쇼호스트 목록 조회 API
     * - ROLE_SHOWHOST인 사용자 리스트 반환
     * - 프론트에서 쇼호스트 선택 dropdown에 사용
     */
    @GetMapping("/showhosts")
    public List<Memberdto.ShowhostResponse> getShowhosts() {
        return memberService.getShowhosts();
    }
}