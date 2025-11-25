package com.sprint.mission.discodeit.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);
}
