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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그인 Rate Limiting 통합 테스트 (Redis)")
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
        loginRateLimitRegistry.resetAttempts(RATE_LIMIT_TEST_IP);
        if (userRepository.findByUsername(TEST_USERNAME).isEmpty()) {
            userRepository.save(new User(TEST_USERNAME, TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD), null));
        }
    }

    @AfterEach
    void tearDown() {
        loginRateLimitRegistry.resetAttempts(RATE_LIMIT_TEST_IP);
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 실패 시 X-RateLimit-Remaining 헤더가 감소한다")
    void login_failure_decrementsRemainingAttempts() throws Exception {
        int maxAttempts = rateLimitProperties.maxAttempts();

        // 1회 실패
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(RATE_LIMIT_TEST_IP))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", "wrongPassword"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("X-RateLimit-Remaining", String.valueOf(maxAttempts - 1)));
    }

    @Test
    @DisplayName("최대 시도 횟수 초과 시 429 Too Many Requests 반환")
    void login_exceedsMaxAttempts_returns429() throws Exception {
        int maxAttempts = rateLimitProperties.maxAttempts();

        // maxAttempts 만큼 실패 시도
        for (int i = 0; i < maxAttempts; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .with(remoteAddr(RATE_LIMIT_TEST_IP))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        // 차단된 상태에서 추가 요청 -> 429 발생 확인
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(RATE_LIMIT_TEST_IP))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD)) // 비밀번호가 맞아도 차단됨
            .andExpect(status().isTooManyRequests())
            .andReturn();

        String retryAfter = result.getResponse().getHeader("Retry-After");
        assertThat(retryAfter).isNotNull();
        assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);
    }

    @Test
    @DisplayName("로그인 성공 시 Rate Limit 카운터 초기화")
    void login_success_resetsRateLimit() throws Exception {
        // 2회 실패 유도
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(RATE_LIMIT_TEST_IP))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", "wrongPassword"));
        }

        // 성공 로그인
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(RATE_LIMIT_TEST_IP))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk());

        // 레지스트리 상태 직접 확인 (Redis에서 초기화되었는지)
        assertThat(loginRateLimitRegistry.getRemainingAttempts(RATE_LIMIT_TEST_IP))
            .isEqualTo(rateLimitProperties.maxAttempts());
    }

    @Test
    @DisplayName("다른 IP는 Rate Limit에 영향받지 않음")
    void login_differentIp_notAffected() throws Exception {
        String anotherIp = "10.0.0.200";
        loginRateLimitRegistry.resetAttempts(anotherIp);
        int maxAttempts = rateLimitProperties.maxAttempts();

        // 첫 번째 IP 차단시킴
        for (int i = 0; i < maxAttempts; i++) {
            mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(RATE_LIMIT_TEST_IP))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", "wrongPassword"));
        }

        // 다른 IP는 정상 동작
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .with(remoteAddr(anotherIp))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk());

        loginRateLimitRegistry.resetAttempts(anotherIp);
    }

    private RequestPostProcessor remoteAddr(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
