package com.sprint.mission.discodeit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 테스트용 최소 보안 설정.
 *
 * <p>@WebMvcTest 슬라이스 테스트에서 @Import를 통해 명시적으로 사용됩니다.
 * 슬라이스 테스트는 컨트롤러 로직만 검증하므로, 복잡한 보안 설정(JWT, Rate Limiting 등)은
 * 통합 테스트(@SpringBootTest)에서 검증합니다.</p>
 *
 * <p>이 설정은 production 코드에 @Profile을 추가하지 않고,
 * 테스트 코드에서 명시적으로 보안 설정을 제어하기 위한 것입니다.</p>
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .build();
    }
}
