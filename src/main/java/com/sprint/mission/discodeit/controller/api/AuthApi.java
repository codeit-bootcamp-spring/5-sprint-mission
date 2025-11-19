package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  ResponseEntity<CsrfToken> getCsrfToken(CsrfToken csrfToken);
} 