package com.sprint.mission.discodeit.auth.presentation;

import com.sprint.mission.discodeit.auth.presentation.dto.JwtResponse;
import com.sprint.mission.discodeit.auth.presentation.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "CSRF 토큰 요청", description = "CSRF 토큰을 쿠키로 발급받습니다.")
    @ApiResponse(responseCode = "204", description = "CSRF 토큰이 쿠키에 설정됨")
    void getCsrfToken(@Parameter(hidden = true) CsrfToken csrfToken);

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = JwtResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 리프레시 토큰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    JwtResponse refresh(
        @Parameter(hidden = true) HttpServletRequest request,
        @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(summary = "사용자 권한 수정", description = "관리자가 사용자의 권한을 변경합니다. (ADMIN 권한 필요)")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "권한 변경 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (ADMIN 권한 필요)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    UserDto updateRole(
        @RequestBody(description = "권한 수정 요청 정보")
        UserRoleUpdateRequest request
    );
}
