package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(
      summary = "CSRF 토큰 발급",
      description = "CSR 환경에서 사용할 CSRF 토큰을 쿠키(XSRF-TOKEN)로 발급합니다."
  )
  @ApiResponse(
      responseCode = "203",
      description = "CSRF 토큰 발급 성공 (쿠키 XSRF-TOKEN에 담겨 반환)",
      content = @Content
  )
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken
  );

  @Operation(
      summary = "현재 로그인된 사용자 정보 조회",
      description = "세션(JSESSIONID) 기반으로 현재 인증된 사용자의 기본 정보를 조회합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "현재 사용자 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "인증되지 않은 요청",
          content = @Content
      )
  })
  ResponseEntity<UserDto> me(
      @Parameter(hidden = true) DiscodeitUserDetails principal
  );

  @Operation(
      summary = "사용자 권한 변경",
      description = "관리자가 특정 사용자의 권한(Role)을 변경합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "권한 변경 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      )
  })
  ResponseEntity<UserDto> updateRole(
      @Parameter(description = "사용자 권한 변경 요청")
      UserRoleUpdateRequest request
  );
}