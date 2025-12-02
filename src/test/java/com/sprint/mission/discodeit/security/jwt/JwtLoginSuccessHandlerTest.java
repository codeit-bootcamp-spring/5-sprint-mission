package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.audit.AuthAuditService;
import com.sprint.mission.discodeit.security.audit.AuthMetricsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtLoginSuccessHandler 단위 테스트")
class JwtLoginSuccessHandlerTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private AuthAuditService authAuditService;

    @Mock
    private AuthMetricsService authMetricsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private ObjectMapper objectMapper;
    private JwtLoginSuccessHandler handler;
    private DiscodeitUserDetails userDetails;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        handler = new JwtLoginSuccessHandler(
            objectMapper, tokenProvider, jwtRegistry, authAuditService, authMetricsService);

        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
        userDetails = new DiscodeitUserDetails(userDto, "password");
    }

    private StringWriter setupResponseWriter() throws Exception {
        StringWriter responseWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));
        return responseWriter;
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 200 상태 코드를 설정한다")
    void onAuthenticationSuccess_SetsOkStatus() throws Exception {
        // given
        setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(response).should().setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - JSON Content-Type을 설정한다")
    void onAuthenticationSuccess_SetsJsonContentType() throws Exception {
        // given
        setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - Refresh 토큰 쿠키를 설정한다")
    void onAuthenticationSuccess_SetsRefreshTokenCookie() throws Exception {
        // given
        setupResponseWriter();
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", "refresh-token");
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token")).willReturn(refreshCookie);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(response).should().addCookie(refreshCookie);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - JwtRegistry에 JWT 정보를 등록한다")
    void onAuthenticationSuccess_RegistersJwtInformation() throws Exception {
        // given
        setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtRegistry).should().registerJwtInformation(any());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 감사 로그를 기록한다")
    void onAuthenticationSuccess_LogsAudit() throws Exception {
        // given
        setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(authAuditService).should().logLoginSuccess(
            userDetails.getUserDto().id(),
            userDetails.getUsername(),
            request
        );
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 메트릭을 기록한다")
    void onAuthenticationSuccess_RecordsMetrics() throws Exception {
        // given
        setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(authMetricsService).should().recordLoginSuccess();
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 응답 본문에 accessToken이 포함된다")
    void onAuthenticationSuccess_ResponseContainsAccessToken() throws Exception {
        // given
        StringWriter responseWriter = setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails)).willReturn("access-token");
        given(tokenProvider.generateRefreshToken(userDetails)).willReturn("refresh-token");
        given(tokenProvider.generateRefreshTokenCookie("refresh-token"))
            .willReturn(new Cookie("REFRESH_TOKEN", "refresh-token"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        String responseBody = responseWriter.toString();
        assertThat(responseBody).contains("access-token");
        assertThat(responseBody).contains("testuser");
    }

    @Test
    @DisplayName("onAuthenticationSuccess - Principal이 DiscodeitUserDetails가 아니면 토큰을 생성하지 않는다")
    void onAuthenticationSuccess_InvalidPrincipal_DoesNotGenerateToken() throws Exception {
        // given
        given(authentication.getPrincipal()).willReturn("invalid-principal");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(tokenProvider).shouldHaveNoInteractions();
        then(jwtRegistry).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("onAuthenticationSuccess - JOSEException 발생 시 에러 응답을 반환한다")
    void onAuthenticationSuccess_JoseException_ReturnsErrorResponse() throws Exception {
        // given
        StringWriter responseWriter = setupResponseWriter();
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(tokenProvider.generateAccessToken(userDetails))
            .willThrow(new JOSEException("Token generation failed"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        then(response).should().setStatus(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(500);

        String responseBody = responseWriter.toString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertThat(jsonNode.has("code")).isTrue();
        assertThat(jsonNode.get("code").asText()).isEqualTo("JWT_GENERATION_FAILED");
    }
}
