package com.sprint.mission.discodeit.controller; // 컨트롤러가 속한 패키지 선언

import com.sprint.mission.discodeit.dto.request.LoginRequest; // 로그인 요청 DTO 임포트
import com.sprint.mission.discodeit.entity.User; // 로그인 성공 시 반환할 사용자 엔티티 임포트
import com.sprint.mission.discodeit.service.AuthService; // 인증 비즈니스 로직 서비스 임포트
import lombok.RequiredArgsConstructor; // final 필드 생성자 자동 생성 애너테이션 임포트
import org.springframework.http.ResponseEntity; // 응답 본문/상태를 감싸는 ResponseEntity 임포트
import org.springframework.web.bind.annotation.*; // REST 컨트롤러 관련 애너테이션 임포트

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

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "자격 증명(아이디/비밀번호 등)을 제출해 로그인합니다."
        , operationId = "loginUser"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<User> login(
        @RequestBody LoginRequest loginRequest
    ) {
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
}
