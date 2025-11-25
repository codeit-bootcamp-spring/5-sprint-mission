package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build(); // 203
    }

    @GetMapping("me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

        UUID userId = userDetails.getUserResponse().getId();

        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new IllegalStateException("현재 로그인한 사용자를 찾을 수 없습니다."));

        UserResponse userResponse = UserResponse.success(user);

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("role")
    public ResponseEntity<UserResponse> updateUserRole(
            @Valid @RequestBody UserRoleUpdateRequest request) {
        log.info("[Controller] 권한 수정 요청: {}", request);
        UserResponse userDto = authService.updateUserRole(request);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtDto> refreshToken(
            @CookieValue(JwtProperties.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse response) {

        log.info("토큰 재발급 요청");

        JwtInformation jwtInformation = authService.refreshToken(refreshToken);

        Cookie cookie = jwtTokenProvider.generateRefreshTokenCookie(jwtInformation.getRefreshToken());
        response.addCookie(cookie);

        JwtDto body = new JwtDto(
                jwtInformation.getUserResponse(),
                jwtInformation.getAccessToken()
        );

        return ResponseEntity.ok(body);
    }
}
