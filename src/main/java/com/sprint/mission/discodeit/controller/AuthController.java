package com.sprint.mission.discodeit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {
	private final AuthService authService;

	@GetMapping("/csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		String tokenValue = csrfToken.getToken();
		log.debug("Csrf 토큰 요청: {}", tokenValue);
		return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(null);
	}

	@GetMapping("/me")
	public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
		return ResponseEntity.ok(userDetails.getUserDto());
	}

	@PutMapping("/role")
	public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
		return ResponseEntity.ok(authService.updateRole(request));
	}
}
