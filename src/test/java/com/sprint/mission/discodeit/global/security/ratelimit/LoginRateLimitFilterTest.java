package com.sprint.mission.discodeit.global.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginRateLimitFilter 단위 테스트")
class LoginRateLimitFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private LoginRateLimitFilter filter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new LoginRateLimitFilter(rateLimiterService, objectMapper);
    }

    @Test
    @DisplayName("doFilterInternal - 로그인 요청이 아니면 필터를 통과한다")
    void doFilterInternal_NonLoginRequest_PassesThrough() throws Exception {
        // given
        given(request.getRequestURI()).willReturn("/api/users");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        then(rateLimiterService).should(never()).isBlocked(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("doFilterInternal - GET 요청은 로그인 경로여도 필터를 통과한다")
    void doFilterInternal_GetLoginRequest_PassesThrough() throws Exception {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("GET");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        then(rateLimiterService).should(never()).isBlocked(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("doFilterInternal - 차단된 요청은 429 상태를 반환한다")
    void doFilterInternal_BlockedRequest_Returns429() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(true);
        given(rateLimiterService.getBlockedSecondsRemaining(clientIp)).willReturn(300L);
        given(response.getWriter()).willReturn(new PrintWriter(new StringWriter()));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(response).should().setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
        then(filterChain).should(never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal - 차단된 요청에 Retry-After 헤더를 설정한다")
    void doFilterInternal_BlockedRequest_SetsRetryAfterHeader() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(true);
        given(rateLimiterService.getBlockedSecondsRemaining(clientIp)).willReturn(120L);
        given(response.getWriter()).willReturn(new PrintWriter(new StringWriter()));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(response).should().setHeader("Retry-After", "120");
    }

    @Test
    @DisplayName("doFilterInternal - 로그인 시도를 기록하고 남은 횟수 헤더를 설정한다")
    void doFilterInternal_RecordsAttemptAndSetsHeader() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(false);
        given(rateLimiterService.getRemainingAttempts(clientIp)).willReturn(4);
        given(response.getStatus()).willReturn(HttpStatus.UNAUTHORIZED.value());

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(rateLimiterService).should().recordAttempt(clientIp);
        then(response).should().setHeader("X-RateLimit-Remaining", "4");
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal - 로그인 성공 시 시도 횟수를 초기화한다")
    void doFilterInternal_SuccessfulLogin_ResetsAttempts() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(false);
        given(rateLimiterService.getRemainingAttempts(clientIp)).willReturn(4);
        given(response.getStatus()).willReturn(HttpStatus.OK.value());

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(rateLimiterService).should().resetAttempts(clientIp);
    }

    @Test
    @DisplayName("doFilterInternal - 로그인 실패 시 시도 횟수를 초기화하지 않는다")
    void doFilterInternal_FailedLogin_DoesNotResetAttempts() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(false);
        given(rateLimiterService.getRemainingAttempts(clientIp)).willReturn(4);
        given(response.getStatus()).willReturn(HttpStatus.UNAUTHORIZED.value());

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(rateLimiterService).should(never()).resetAttempts(clientIp);
    }

    @Test
    @DisplayName("extractClientKey - X-Forwarded-For 헤더가 있으면 첫 번째 IP를 사용한다")
    void extractClientKey_WithXForwardedFor_UsesFirstIp() throws Exception {
        // given
        String forwardedFor = "10.0.0.1, 10.0.0.2";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(forwardedFor);
        given(rateLimiterService.isBlocked("10.0.0.1")).willReturn(false);
        given(rateLimiterService.getRemainingAttempts("10.0.0.1")).willReturn(5);
        given(response.getStatus()).willReturn(HttpStatus.UNAUTHORIZED.value());

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(rateLimiterService).should().recordAttempt("10.0.0.1");
    }

    @Test
    @DisplayName("extractClientKey - X-Forwarded-For가 없으면 remoteAddr을 사용한다")
    void extractClientKey_WithoutXForwardedFor_UsesRemoteAddr() throws Exception {
        // given
        String remoteAddr = "192.168.1.100";
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(remoteAddr);
        given(rateLimiterService.isBlocked(remoteAddr)).willReturn(false);
        given(rateLimiterService.getRemainingAttempts(remoteAddr)).willReturn(5);
        given(response.getStatus()).willReturn(HttpStatus.UNAUTHORIZED.value());

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(rateLimiterService).should().recordAttempt(remoteAddr);
    }

    @Test
    @DisplayName("handleBlockedRequest - 에러 응답을 JSON으로 작성한다")
    void handleBlockedRequest_WritesJsonErrorResponse() throws Exception {
        // given
        String clientIp = "192.168.1.1";
        StringWriter responseWriter = new StringWriter();
        given(request.getRequestURI()).willReturn("/api/auth/login");
        given(request.getMethod()).willReturn("POST");
        given(request.getHeader("X-Forwarded-For")).willReturn(null);
        given(request.getRemoteAddr()).willReturn(clientIp);
        given(rateLimiterService.isBlocked(clientIp)).willReturn(true);
        given(rateLimiterService.getBlockedSecondsRemaining(clientIp)).willReturn(60L);
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        String responseBody = responseWriter.toString();
        assertThat(responseBody).contains("TOO_MANY_REQUESTS");
        assertThat(responseBody).contains("retryAfterSeconds");
    }
}
