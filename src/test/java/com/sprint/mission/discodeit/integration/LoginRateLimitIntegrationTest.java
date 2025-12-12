package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그인 Rate Limiting 통합 테스트")
class LoginRateLimitIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimitRegistry loginRateLimitRegistry;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    private static final String TEST_USERNAME = "ratelimituser";
    private static final String TEST_EMAIL = "ratelimit@example.com";
    private static final String TEST_PASSWORD = "P@ssw0rd!";
    private static final String RATE_LIMIT_TEST_IP = "10.0.0.100";

    @BeforeEach
    void setUp() {
        // Rate limit 상태 초기화
        loginRateLimitRegistry.resetAttempts(RATE_LIMIT_TEST_IP);

        // 테스트 사용자 생성
        if (userRepository.findByUsername(TEST_USERNAME).isEmpty()) {
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);
        }
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 Rate limit 상태 정리
        loginRateLimitRegistry.resetAttempts(RATE_LIMIT_TEST_IP);
    }

    @Test
    @DisplayName("로그인 실패 시 남은 시도 횟수가 X-RateLimit-Remaining 헤더에 반환된다")
    void login_failure_returnsRemainingAttemptsHeader() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                    return request;
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", "wrongPassword"))
            .andExpect(status().isUnauthorized())
            .andReturn();

        String remaining = result.getResponse().getHeader("X-RateLimit-Remaining");
        assertThat(remaining).isNotNull();
        int remainingAttempts = Integer.parseInt(remaining);
        assertThat(remainingAttempts).isLessThan(rateLimitProperties.maxAttempts());
    }

    @Test
    @DisplayName("최대 시도 횟수 초과 시 429 Too Many Requests 반환")
    void login_exceedsMaxAttempts_returns429() throws Exception {
        int maxAttempts = rateLimitProperties.maxAttempts();

        // maxAttempts 만큼 실패 시도
        for (int i = 0; i < maxAttempts; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .with(request -> {
                        request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                        return request;
                    })
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        // 차단된 상태에서 추가 요청 시 429 반환
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                    return request;
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isTooManyRequests())
            .andReturn();

        // Retry-After 헤더 확인
        String retryAfter = result.getResponse().getHeader("Retry-After");
        assertThat(retryAfter).isNotNull();
        assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);

        // 응답 본문에 retryAfterSeconds 포함 확인
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("retryAfterSeconds");
    }

    @Test
    @DisplayName("로그인 성공 시 Rate Limit 카운터 초기화")
    void login_success_resetsRateLimit() throws Exception {
        // 먼저 몇 번 실패
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .with(request -> {
                        request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                        return request;
                    })
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        // 로그인 성공
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                    return request;
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk());

        // Rate limit 초기화 확인
        assertThat(loginRateLimitRegistry.getRemainingAttempts(RATE_LIMIT_TEST_IP))
            .isEqualTo(rateLimitProperties.maxAttempts());
    }

    @Test
    @DisplayName("차단된 상태에서는 올바른 자격 증명으로도 로그인 불가")
    void login_whenBlocked_rejectsEvenValidCredentials() throws Exception {
        int maxAttempts = rateLimitProperties.maxAttempts();

        // maxAttempts 만큼 실패 시도하여 차단
        for (int i = 0; i < maxAttempts; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .with(request -> {
                        request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                        return request;
                    })
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        // 차단된 상태에서 올바른 자격 증명으로 로그인 시도
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                    return request;
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("다른 IP는 Rate Limit에 영향받지 않음")
    void login_differentIp_notAffectedByOtherIpRateLimit() throws Exception {
        String anotherIp = "10.0.0.200";
        loginRateLimitRegistry.resetAttempts(anotherIp);

        int maxAttempts = rateLimitProperties.maxAttempts();

        // 첫 번째 IP에서 maxAttempts 만큼 실패하여 차단
        for (int i = 0; i < maxAttempts; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .with(request -> {
                        request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                        return request;
                    })
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        // 다른 IP에서는 정상 로그인 가능
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(anotherIp);
                    return request;
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk());

        // 정리
        loginRateLimitRegistry.resetAttempts(anotherIp);
    }
}
