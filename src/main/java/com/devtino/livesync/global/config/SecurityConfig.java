package com.devtino.livesync.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 추가
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtTokenProvider jwtTokenProvider) throws Exception {

        http
                // CSRF 비활성화 (JWT 기반이라 필요 없음)
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안함 (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /*
                         * 인증 없이 허용
                         */
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        /*
                         * 관리자 전용
                         */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        /*
//                         * 파일 관련 권한
//                         */
//
//                        // 내 파일 조회 (로그인만)
//                        .requestMatchers("/files/my").authenticated()
//
//                        // 업로드 / 삭제 → 관리자만
//                        .requestMatchers("/files/upload").hasRole("ADMIN")
//                        .requestMatchers("/files/{id}").hasRole("ADMIN")
//
//                        // 전체 조회 → 관리자만
//                        .requestMatchers("/files/**").hasRole("ADMIN")

                        /*
                         * 일정
                         */

                        // 생성 → 관리자만
                        .requestMatchers("/schedules").hasRole("ADMIN")

                        // 조회 → 관리자 + 쇼호스트
                        .requestMatchers("/schedules/**").hasAnyRole("ADMIN", "SHOWHOST")

                        /*
                         * 나머지
                         */
                        .anyRequest().authenticated()
                )

                // JWT 필터
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}