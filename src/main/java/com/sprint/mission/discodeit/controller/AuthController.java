package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// --- Swagger(OpenAPI) 애너테이션 임포트 ---
import io.swagger.v3.oas.annotations.Operation; // API 요약/설명 애너테이션
import io.swagger.v3.oas.annotations.tags.Tag; // API 그룹(태그) 애너테이션
import io.swagger.v3.oas.annotations.media.Content; // 요청/응답 콘텐츠 메타데이터
import io.swagger.v3.oas.annotations.media.Schema; // 스키마 정의
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 응답 코드 정의
import io.swagger.v3.oas.annotations.responses.ApiResponses; // 응답 묶음
// -----------------------------------------

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Auth",
    description = "인증 관련 API (로그인)"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping(
        value = "/login",
        consumes = "application/json", // 요청 본문 타입을 명시
        produces = "application/json" // 응답 본문 타입을 명시
         )
    @Operation(
        summary = "로그인",
        description = "자격 증명(아이디/비밀번호 등)을 제출해 로그인합니다."
        , operationId = "login"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content =
                {
                    @Content(schema = @Schema(implementation = User.class)),
                    @Content(mediaType = MediaType.ALL_VALUE)
                }),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문"
            , content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
            @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
        }),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<User> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
}
