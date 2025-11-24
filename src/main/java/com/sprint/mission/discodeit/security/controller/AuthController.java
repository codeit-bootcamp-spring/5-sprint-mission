package com.sprint.mission.discodeit.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthService authService;

	@Override
	@GetMapping("csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		csrfToken.getToken();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("me")
	public ResponseEntity<UserDto> me(
		@AuthenticationPrincipal DiscodeitUserDetails authentication
	) {
		return ResponseEntity.ok(authentication.getUserDto());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("role")
	public UserDto role(
		UserRoleUpdateRequest request
	) {
		return authService.updateRole(request.getUserId(), request.getNewRole());
	}
}
