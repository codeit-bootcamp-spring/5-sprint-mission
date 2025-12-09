package com.sprint.mission.discodeit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.user.JwtDto;
import com.sprint.mission.discodeit.dto.user.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping("/csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		String tokenValue = csrfToken.getToken();
		log.debug("Csrf 토큰 요청: {}", tokenValue);
		return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(null);
	}

	@PostMapping("/refresh")
	public ResponseEntity<JwtDto> refreshToken(
		@CookieValue(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
		HttpServletResponse response
	) {
		log.info("Refresh Token: {}", refreshToken);
		JwtInformation jwtInformation = authService.refreshToken(refreshToken);
		Cookie cookie = jwtTokenProvider.genereateRefreshTokenCookie(jwtInformation.getRefreshToken());
		response.addCookie(cookie);

		JwtDto jwtDto = new JwtDto(jwtInformation.getUserDto(), jwtInformation.getAccessToken());

		return ResponseEntity.ok(jwtDto);
	}

	@PutMapping("/role")
	public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
		return ResponseEntity.ok(authService.updateRole(request));
	}
}
