package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;
	private final UserService userService;

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
	private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 14 * 24 * 60 * 60; // 14일

	@GetMapping("csrf-token")
	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
		log.debug("CSRF 토큰 요청");
		log.trace("CSRF 토큰: {}", csrfToken.getToken());
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}

	@GetMapping("me")
	public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
		log.info("내 정보 조회 요청");
		UUID userId = userDetails.getUserDto().id();
		UserDto userDto = userService.find(userId);
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userDto);
	}

	@PutMapping("role")
	public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
		log.info("권한 수정 요청");
		UserDto userDto = authService.updateRole(request);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userDto);
	}

	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(
		@Valid @ModelAttribute LoginRequest loginRequest,
		HttpServletResponse response
	) {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginRequest.username(),
					loginRequest.password()
				)
			);

			DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();
			String username = userDetails.getUsername();
			UserDto userDto = userDetails.getUserDto();

			// 3. JWT 토큰 생성
			String accessToken = jwtTokenProvider.createAccessToken(username);
			String refreshToken = jwtTokenProvider.createRefreshToken(username);

			// 4. Refresh Token을 HttpOnly 쿠키에 저장
			Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
			refreshTokenCookie.setHttpOnly(true);
			refreshTokenCookie.setSecure(false);
			refreshTokenCookie.setPath("/");
			refreshTokenCookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
			refreshTokenCookie.setAttribute("SameSite", "Strict");
			response.addCookie(refreshTokenCookie);

			// 5. Access Token과 사용자 정보 반환
			JwtDto jwtDto = new JwtDto(userDto, accessToken);
			log.info("로그인 성공: username={}", username);

			return ResponseEntity.ok(jwtDto);

		} catch (AuthenticationException e) {
			log.error("로그인 실패: {}", e.getMessage());
			throw e;
		}
	}
}
