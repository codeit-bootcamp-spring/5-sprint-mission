package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/auth")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급", description = "CSR 환경에서 사용할 CSRF 토큰을 쿠키로 발급합니다.")
  @ApiResponse(
      responseCode = "203",
      description = "CSRF 토큰 발급 성공 (쿠키 XSRF-TOKEN에 담겨 반환)",
      content = @Content
  )
  @GetMapping("/csrf-token")
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken
  );
}
