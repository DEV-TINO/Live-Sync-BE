package com.devtino.livesync.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * 1. Authorization header에서 토큰 추출
         */
        String token = resolveToken(request);

        /*
         * 2. 토큰 검증
         */
        if (token != null && jwtTokenProvider.validateToken(token)) {

            /*
             * 3. 사용자 정보 추출
             */
            Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));
            String role = jwtTokenProvider.getRole(token);
            Long workspaceId = jwtTokenProvider.getWorkspaceId(token);

            /*
             * 4. ROLE_ prefix 통일 (Spring Security 규칙)
             */
            String authorityRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(authorityRole));

            /*
             * 5. Authentication 생성
             * principal = memberId
             * credentials = null
             * details = workspaceId (핵심)
             */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            memberId,
                            null,
                            authorities
                    );

            /*
             * 6. SecurityContext 저장
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /*
     * Bearer Token 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}