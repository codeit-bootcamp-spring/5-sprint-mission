package com.sprint.mission.discodeit.security.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import com.sprint.mission.discodeit.security.dto.JwtDto;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

	public ResponseEntity<JwtDto> getRefreshToken(String refreshToken);

}