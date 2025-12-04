package com.sprint.mission.discodeit.global.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createDiscodeitUserDetails;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 단위 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ObjectMapper objectMapper;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationFilter(tokenProvider, jwtRegistry, userDetailsService, objectMapper);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal - Authorization 헤더가 없으면 필터를 통과한다")
    void doFilterInternal_NoAuthHeader_PassesThrough() throws Exception {
        // given
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal - Bearer 접두사가 없으면 필터를 통과한다")
    void doFilterInternal_NoBearerPrefix_PassesThrough() throws Exception {
        // given
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Basic token");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal - 유효하지 않은 토큰이면 401 응답을 반환한다")
    void doFilterInternal_InvalidToken_ReturnsUnauthorized() throws Exception {
        // given
        String token = "invalid-token";
        StringWriter responseWriter = new StringWriter();
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
        given(tokenProvider.validateAccessToken(token)).willReturn(false);
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(response).should().setStatus(HttpStatus.UNAUTHORIZED.value());
        then(filterChain).should(never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal - 레지스트리에 없는 토큰이면 401 응답을 반환한다")
    void doFilterInternal_TokenNotInRegistry_ReturnsUnauthorized() throws Exception {
        // given
        String token = "valid-but-not-registered";
        StringWriter responseWriter = new StringWriter();
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
        given(tokenProvider.validateAccessToken(token)).willReturn(true);
        given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(false);
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(response).should().setStatus(HttpStatus.UNAUTHORIZED.value());
        then(filterChain).should(never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal - 유효한 토큰이면 SecurityContext에 인증 정보를 설정한다")
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        // given
        String token = "valid-token";
        String username = "testuser";
        UserDto userDto = createUserDto(UUID.randomUUID(), username, "test@example.com");
        DiscodeitUserDetails userDetails = createDiscodeitUserDetails(userDto);

        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
        given(tokenProvider.validateAccessToken(token)).willReturn(true);
        given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
        given(tokenProvider.getUsernameFromToken(token)).willReturn(username);
        given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
    }

    @Test
    @DisplayName("doFilterInternal - 예외 발생 시 SecurityContext를 초기화하고 401 응답을 반환한다")
    void doFilterInternal_ExceptionThrown_ClearsContextAndReturnsUnauthorized() throws Exception {
        // given
        String token = "valid-token";
        StringWriter responseWriter = new StringWriter();
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
        given(tokenProvider.validateAccessToken(token)).willReturn(true);
        given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
        given(tokenProvider.getUsernameFromToken(token)).willThrow(new RuntimeException("Token error"));
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(response).should().setStatus(HttpStatus.UNAUTHORIZED.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal - 에러 응답에 INVALID_TOKEN 코드가 포함된다")
    void doFilterInternal_ErrorResponse_ContainsInvalidTokenCode() throws Exception {
        // given
        String token = "invalid-token";
        StringWriter responseWriter = new StringWriter();
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
        given(tokenProvider.validateAccessToken(token)).willReturn(false);
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        String responseBody = responseWriter.toString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertThat(jsonNode.has("code")).isTrue();
        assertThat(jsonNode.get("code").asText()).isEqualTo("INVALID_TOKEN");
    }
}
