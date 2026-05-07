package com.devtino.livesync.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 로컬 테스트를 위해 임시 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용을 위한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/oauth2/**", // 공통 접근 경로
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/auth/**"
                        ).permitAll() // 로그인 화면, 소셜 로그인 요청은 허용

                        // 파일 권한 설정 (관리자만 업로드/삭제, 쇼호스트는 조회만)

                        // 1. 내 파일 조회는 로그인 사용자 모두 허용 (SHOWHOST + ADMIN)
                        // - 반드시 /files/** 보다 먼저 선언해야 정상 동작
                        .requestMatchers("/files/my").authenticated()

                        // 2. 파일 업로드 → 관리자만 가능
                        .requestMatchers("/files/upload").hasRole("ADMIN")

                        // 3. 파일 삭제 → 관리자만 가능
                        .requestMatchers("/files/{id}").hasRole("ADMIN")

                        // 4. 전체 파일 조회 → 관리자만 가능
                        // - 이 규칙이 /files/** 전체를 덮음
                        .requestMatchers("/files/**").hasRole("ADMIN")

                        // 관리자 전용 api
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 쇼호스트 전용 api (쇼호스트와 관리자 모두 가능)
                        .requestMatchers("/api/showhost/**").hasAnyRole("SHOWHOST", "ADMIN")

                        // 그 외 일반적인 api는 로그인만 해야 접근 가능
                        .anyRequest().authenticated()

                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

//                .oauth2Login(oauth2 -> oauth2
//                                //.loginPage("/login") // 나중에 커스텀 로그인 페이지를 만들면 설정
//                                .defaultSuccessUrl("/home") // 로그인 성공 후 이동할 경로
//                        // .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // 나중에 유저 정보를 DB에 저장할 때 사용할 서비스 연결 (Step 5)
//                );

        return http.build();
    }

    // 비밀번호 암호화를 위한 Bean 등록 (GeneralAuthService에서 사용)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}