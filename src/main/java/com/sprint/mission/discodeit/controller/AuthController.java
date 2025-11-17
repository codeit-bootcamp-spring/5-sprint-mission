package com.sprint.mission.discodeit.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.domain.dto.command.UpdateRoleCommand;
import com.sprint.mission.discodeit.domain.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
@Slf4j
public class AuthController {

	private final AuthService authService;

	@GetMapping("/csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken, HttpServletResponse response) {
		String tokenValue = csrfToken.getToken();
		log.debug("CSRF 토큰 요청: {}", tokenValue);

		ResponseCookie cookie = getCsrfCookie(tokenValue);
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity
		  .status(NON_AUTHORITATIVE_INFORMATION)
		  .build();
	}

	@GetMapping("/me")
	private ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
		UserDto userDto = userDetails.getUserDto();

		return ResponseEntity.ok(userDto);
	}

	@PutMapping("/role")
	private ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {

		UserDto userDto = authService.updateRole(UpdateRoleCommand.of(request));

		return ResponseEntity.ok(userDto);

	}

	private ResponseCookie getCsrfCookie(String tokenValue) {

		return ResponseCookie.from("XSRF-TOKEN", tokenValue)
		  .httpOnly(false)
		  .secure(false)
		  .path("/")
		  .sameSite("Lax")
		  .build();
	}
}
