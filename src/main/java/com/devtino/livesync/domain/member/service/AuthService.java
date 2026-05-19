package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.dto.Memberdto.LoginRequest;
import com.devtino.livesync.domain.member.dto.Memberdto.MemberResponse;
import com.devtino.livesync.domain.member.dto.Memberdto.SignupRequest;

public interface AuthService
{

    // 공통적인 회원가입
    void signup(SignupRequest request);
    Memberdto.JwtTokenResponse login(Memberdto.LoginRequest request);

    void logout(Long memberId);
    Memberdto.JwtTokenResponse reissue(String refreshToken);
}
