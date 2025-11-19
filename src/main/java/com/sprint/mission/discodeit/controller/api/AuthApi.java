package com.sprint.mission.discodeit.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

	ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

	ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails);
}
