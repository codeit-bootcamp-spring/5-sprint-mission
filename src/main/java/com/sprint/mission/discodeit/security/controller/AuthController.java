package com.sprint.mission.discodeit.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.controller.api.AuthApi;
import com.sprint.mission.discodeit.security.dto.JwtDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;

	@Override
	@PostMapping("refresh")
	public ResponseEntity<JwtDto> getRefreshToken(
		@CookieValue(JwtTokenProvider.REQUEST_TOKEN_COOKIE_NAME) String refreshToken
	) {
		JwtDto jwtDto = authService.reGenerateToken(refreshToken);
		return ResponseEntity.ok(jwtDto);
	}

	@Override
	@GetMapping("csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		csrfToken.getToken();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("role")
	public UserDto role(
		@RequestBody UserRoleUpdateRequest request
	) {
		log.debug("role update: {}", request.getUserId());
		log.debug("role update: {}", request.getNewRole());
		return authService.updateRole(request.getUserId(), request.getNewRole());
	}
}
