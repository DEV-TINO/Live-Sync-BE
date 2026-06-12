package com.devtino.livesync.domain.member.controller;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
     * 쇼호스트 목록 조회
     * workspaceId 기준 조회
     */
    @GetMapping("/showhosts")
    public List<Memberdto.ShowhostResponse> getShowhosts(
            @RequestParam Long workspaceId
    ) {
        return memberService.getShowhosts(workspaceId);
    }
}