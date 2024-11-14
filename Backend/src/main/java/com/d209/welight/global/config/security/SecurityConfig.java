package com.d209.welight.global.config.security;

import com.d209.welight.global.config.jwt.JwtAuthenticationFilter;
import com.d209.welight.global.error.CommonErrorCode;
import com.d209.welight.global.error.ErrorResponse;
import com.d209.welight.global.util.jwt.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // 이 클래스가 Spring의 Configuration 클래스를 정의함을 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 제공자
    private final ObjectMapper objectMapper;

    @Bean // 이 메서드가 Spring Bean으로 관리되도록 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증을 비활성화 (HTTP Basic Authentication)
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화 (Cross-Site Request Forgery)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 Stateless로 설정 (서버에 세션을 저장하지 않음)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**").permitAll() // Swagger UI 접근 허용
                        .requestMatchers("/v3/api-docs/**").permitAll() // API 문서 접근 허용
                        .requestMatchers("/user/signup/**", "/user/login/**", "/user/token/**").permitAll() // 회원가입, 로그인, 토큰 재발급 접근 허용
                        .requestMatchers("/test/**", "/", "/index.html").permitAll()
                        .requestMatchers("/api/**", "/error").permitAll()// url prefix 관련
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());

                            ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.UNAUTHORIZED);
                            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpStatus.FORBIDDEN.value());

                            ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.FORBIDDEN);
                            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                        }))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build(); // 설정된 HttpSecurity 객체를 빌드하여 반환
    }
}
