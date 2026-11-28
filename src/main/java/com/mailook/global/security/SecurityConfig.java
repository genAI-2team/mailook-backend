package com.mailook.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public JwtDecoder jwtDecoder(JwtProvider provider) {
        return provider.decoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // localStorage의 JWT를 Authorization 헤더로만 받으며 세션·쿠키 인증은 사용하지 않는다.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(cache -> cache.disable())
            .oauth2ResourceServer(resource -> resource.jwt(jwt -> {})
                .authenticationEntryPoint((request, response, error) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"status\":\"fail\",\"message\":\"유효한 JWT로 다시 로그인해 주세요.\"}");
                }))
            .authorizeHttpRequests(auth -> auth
                // [변경 전 — 2026-11-27]
                // .requestMatchers(HttpMethod.POST, "/api/maiLookLogin/login", "/api/maiLookLogin/insert").permitAll()
                // [변경 후 — 2026-11-27] 로그인 새 주소를 공개하고 기존 회원가입 주소는 유지한다.
                // [변경 전 — 2026-11-27] .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/maiLookLogin/insert").permitAll()
                // [변경 후 — 2026-11-27] 로그인과 새 회원가입 주소를 인증 없이 허용한다.
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, error) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"status\":\"fail\",\"message\":\"로그인이 필요합니다.\"}");
                })
                .accessDeniedHandler((request, response, error) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"status\":\"fail\",\"message\":\"요청 권한이 없습니다.\"}");
                }));
        return http.build();
    }
}
