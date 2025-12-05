package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.domain.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
@SuppressWarnings("checkstyle:LineLength")
public interface AuthControllerDocs {

    @Operation(summary = "CSRF 토큰 요청")
    @ApiResponse(responseCode = "204", description = "CSRF 토큰 요청 성공")
    @ApiResponse(responseCode = "400", description = "CSRF 토큰 요청 실패")
    void getCsrfToken(@Parameter(hidden = true) CsrfToken csrfToken);

    @Operation(summary = "사용자 권한 수정")
    @ApiResponse(
        responseCode = "200",
        description = "권한 변경 성공",
        content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    UserDto updateRole(@Parameter(description = "권한 수정 요청 정보") RoleUpdateRequest request);
}
