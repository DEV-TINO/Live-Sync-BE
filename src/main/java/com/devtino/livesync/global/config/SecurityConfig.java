package com.devtino.livesync.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtTokenProvider jwtTokenProvider) throws Exception {

        http
                /*
                 * CSRF 비활성화
                 * JWT 기반 API에서는 세션을 사용하지 않기 때문에 필요 없음
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * 세션 사용 안함 (STATELESS)
                 * 매 요청마다 JWT로 인증 처리
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /*
                         * 인증 없이 접근 가능한 API
                         */
                        .requestMatchers("/api/auth/**").permitAll()

                        /*
                         * Swagger / API Docs 허용
                         */
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        /*
                         * SSE / 알림
                         */
                        .requestMatchers("/notifications/**").authenticated()

                        /*
                         * 관리자 전용 API
                         * ROLE_ADMIN 기준으로 체크
                         */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        /*
                         * 일정 API
                         * 인증만 필요 (권한은 Service에서 처리)
                         */
                        .requestMatchers("/schedules/**").authenticated()

                        /*
                         * 워크스페이스 API
                         * 인증만 필요
                         */
                        .requestMatchers("/workspace/**").authenticated()

                        /*
                         * 그 외 모든 요청은 인증 필요
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * JWT 인증 필터 등록
                 */
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /*
     * 비밀번호 암호화 Bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
