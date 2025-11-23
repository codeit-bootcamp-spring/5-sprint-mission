package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler,
            Http403ForbiddenAccessDeniedHandler accessDeniedHandler,
            SessionRegistry sessionRegistry,
            DiscodeitUserDetailsService discodeitUserDetailsService

    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                // permitAll 경로 설정
                                // 프론트 코드 경로때문에 /** 추가 -> 화면 다시 정상적으로 출력
                                .requestMatchers("/login", "/error", "/", "/**").permitAll()
                        // h2 때문에 추가했음. 배포환경에서는 제거하는 게 좋을 것 같음. ( /** 도 h2 때문)
                                .requestMatchers("/h2-console/**").permitAll() //h2
                                .requestMatchers("/api/auth/csrf-token").permitAll() //csrf-token
                                .requestMatchers("/api/auth/login").permitAll() // 로그인
                                .requestMatchers("/api/auth/logout").permitAll() // 로그아웃
                                //API가 아닌 요청
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입
                                // 나머지 권한 USER 필요
                                .anyRequest().authenticated()
                )
                // h2 때문에 추가했음. 배포환경에서는 제거하는 게 좋을 것 같음.
                .headers(header -> header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .csrf(csrf -> csrf
                        // 로그 아웃은 CSRF 예외처리 필요
                        .ignoringRequestMatchers("/api/auth/logout")
                        // h2 때문에 추가했음. 배포환경에서는 제거하는 게 좋을 것 같음.
                        .ignoringRequestMatchers("/h2-console/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // spring에서 제공하는 강력한 CSRF 토큰 만드는 코드, 최신버전은 유사코드가 삽입됨!
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        // logout 응답값 자동생성용 핸들러
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler)

                )
                .sessionManagement(mgmt -> mgmt
                        .sessionFixation().migrateSession()  // 일반 세션 보안

                        .sessionConcurrency(con -> con       // 동시 로그인 정책
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry)
                        )

                )
                // Remember-Me 설정
                // 간편한 설정 버전!
//                .rememberMe(Customizer.withDefaults())
                .rememberMe(remember -> remember
                        .key("JESSIONID") // 쿠키 이름
                        .tokenValiditySeconds(60*60)
                        .userDetailsService(discodeitUserDetailsService)
                )
        ;
        return http.build();
    }


    // BCrypt 알고리즘을 통해 패스워드 생성 및 복호화를 수행할 객체 생성
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name()) //  높은 권한
                .implies(Role.CHANNEL_MANAGER.name()) // 낮은 권한

                .role(Role.CHANNEL_MANAGER.name())
                .implies(Role.USER.name())

                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


}
