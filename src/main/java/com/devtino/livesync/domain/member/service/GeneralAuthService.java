package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.dto.Memberdto;
import com.devtino.livesync.domain.member.entity.LoginType;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.entity.MemberRole;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.global.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Primary
public class GeneralAuthService implements AuthService
{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /*
     * 일반 사용자 회원가입
     */
    @Override
    @Transactional
    public void signup(Memberdto.SignupRequest request)
    {
        // 1. 중복 체크
        if(memberRepository.findByLoginId(request.getLoginId()).isPresent())
        {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .loginType(LoginType.GENERAL)
                .role(MemberRole.ROLE_USER)
                .build();

        memberRepository.save(member);
    }

    /*
     * 일반 로그인
     */
    @Override
    @Transactional
    public Memberdto.JwtTokenResponse login(Memberdto.LoginRequest request)
    {
        // 1. 아이디로 유저 찾기
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        // 2. passwordEncoder.matches()로 비번 검증
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword()))
        {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getRole().name()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        member.updateRefreshToken(refreshToken);

        return new Memberdto.JwtTokenResponse(accessToken, refreshToken);
    }

    /*
     * 쇼호스트 로그인 (최초 로그인 포함)
     */
    @Transactional
    public Memberdto.JwtTokenResponse showhostLogin(Memberdto.ShowhostLoginRequest request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        if (member.getRole() != MemberRole.ROLE_SHOWHOST) {
            throw new RuntimeException("쇼호스트가 아닙니다.");
        }

        // 최초 로그인 → 비밀번호 설정
        if (member.getPassword() == null) {
            member.updatePassword(passwordEncoder.encode(request.getPassword()));
        } else {
            if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
                throw new RuntimeException("비밀번호 불일치");
            }
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getRole().name()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        member.updateRefreshToken(refreshToken);

        return new Memberdto.JwtTokenResponse(accessToken, refreshToken);
    }

    /*
     * 로그아웃
     */
    @Override
    @Transactional
    public void logout(Long memberId)
    {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        member.updateRefreshToken(null);
    }

    /*
     * 토큰 재발급
     */
    @Override
    @Transactional
    public Memberdto.JwtTokenResponse reissue(String refreshToken)
    {
        if (!jwtTokenProvider.validateToken(refreshToken))
        {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(
                member.getId(),
                member.getRole().name()
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        member.updateRefreshToken(newRefreshToken);

        return new Memberdto.JwtTokenResponse(newAccessToken, newRefreshToken);
    }
}