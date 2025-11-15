package com.sprint.mission.discodeit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
		UserDto dto = authService.login(loginRequest);

		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@GetMapping("/csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		String tokenValue = csrfToken.getToken();
		log.debug("Csrf 토큰 요청: {}", tokenValue);
		return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(null);
	}
}
