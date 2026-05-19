package com.devtino.livesync.domain.member.service;

import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.Map;

//@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService
{

    // MemberRepository 주입 필요 (나중에 DB 저장용)
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        // 1. 기본 제공 기능을 통해 카카오 서버에서 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 카카오 고유 식별자 (필요 시)
        String kakaoId = attributes.get("id").toString();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");
        saveOrUpdate(kakaoId, nickname);

        return oAuth2User;
    }

    private void saveOrUpdate(String kakaoId, String nickname)
    {
        Member member = memberRepository.findByKakaoId(kakaoId)
                .map(entity -> entity.update(nickname))
                .orElse(Member.builder()
                        .kakaoId(kakaoId)
                        .nickname(nickname)
                        .build());

        memberRepository.save(member);
    }

}