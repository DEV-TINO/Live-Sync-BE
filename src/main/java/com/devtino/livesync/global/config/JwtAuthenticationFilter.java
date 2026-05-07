package com.devtino.livesync.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Collections;

// 권한 처리용 import
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 1. 헤더에서 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰에서 memberId 추출
            String memberIdStr = jwtTokenProvider.getMemberId(token);
            Long memberId = Long.parseLong(memberIdStr);

            // 4. 토큰에서 ROLE 정보 추출
            String role = jwtTokenProvider.getRole(token);

            // 5. Spring Security 권한 객체로 변환
            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(role));

            // 6. 인증 객체 생성 (memberId + 권한 포함)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, null, authorities);

            // 7. SecurityContext에 등록 (이걸 해야 인증된 사용자로 인식됨)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. 다음 필터로 진행
        chain.doFilter(request, response);
    }

    /*
     * 헤더에서 "Authorization" : "Bearer {TOKEN}" 패턴을 찾아 토큰만 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}