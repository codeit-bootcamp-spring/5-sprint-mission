package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest; // 생성 요청 DTO 임포트
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest; // 수정 요청 DTO 임포트
import com.sprint.mission.discodeit.entity.ReadStatus; // ReadStatus 엔티티 임포트
import com.sprint.mission.discodeit.service.ReadStatusService; // 서비스 임포트
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 임포트
import org.springframework.http.HttpStatus; // 상태코드 상수 임포트
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // 응답 엔벨로프 임포트
import org.springframework.web.bind.annotation.*; // REST 애너테이션 임포트

import java.util.List; // 리스트 컬렉션 임포트
import java.util.UUID; // UUID 타입 임포트

// -------------------- Swagger(OpenAPI) 임포트(간단 버전) --------------------
import io.swagger.v3.oas.annotations.Operation; // 엔드포인트 요약/설명
import io.swagger.v3.oas.annotations.media.Content; // 응답 Content
import io.swagger.v3.oas.annotations.media.Schema; // 스키마 정의
import io.swagger.v3.oas.annotations.media.ArraySchema; // 배열 스키마
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 단일 응답
import io.swagger.v3.oas.annotations.responses.ApiResponses; // 복수 응답
import io.swagger.v3.oas.annotations.tags.Tag; // 컨트롤러 태그
// ---------------------------------------------------------------------------

@RequiredArgsConstructor // final 필드 생성자를 자동 생성
@RestController // @Controller + @ResponseBody 대체: REST 응답 전용 컨트롤러
@RequestMapping("/api/readStatuses") // 베이스 경로는 기존 그대로 유지
@Tag(name = "ReadStatus", description = "읽음 상태(ReadStatus) 생성/수정/조회 API") // 컨트롤러 태그
public class ReadStatusController { // 컨트롤러 클래스 시작

    private final ReadStatusService readStatusService; // 비즈니스 로직 서비스 의존성

    @PostMapping( // 생성은 POST 메서드로 명시
        consumes = "application/json", // 요청 본문 타입(JSON) 명시
        produces = "application/json" // 응답 본문 타입(JSON) 명시
    )
    @Operation( // 생성 API 문서(간단 요약/설명만)
        summary = "읽음 상태 생성",
        description = "요청 본문(JSON)의 필드로 읽음 상태를 생성합니다.",
        operationId = "createReadStatus"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = {
                @Content(schema = @Schema(implementation = ReadStatus.class)),
                @Content(mediaType = MediaType.ALL_VALUE) // ← 추가
            }
        ),
        @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 형식 오류",
                        content = @Content(mediaType = MediaType.ALL_VALUE)),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ReadStatus> create( // 생성 엔드포인트
        @RequestBody ReadStatusCreateRequest request // 요청 본문을 DTO로 바인딩
    ) {
        ReadStatus createdReadStatus = readStatusService.create(request); // 서비스에 생성 위임
        return ResponseEntity
            .status(HttpStatus.CREATED) // 201 Created
            .body(createdReadStatus); // 생성된 리소스 반환
    }

    @PatchMapping( // 수정은 Patch 메서드로 명시
        path = "/{readStatusId}", // 기존 경로명 유지 + PathVariable 적용
        consumes = "application/json", // 요청 본문 타입(JSON)
        produces = "application/json" // 응답 본문 타입(JSON)
    )
    @Operation( // 수정 API 문서(간단 요약/설명만)
        summary = "읽음 상태 수정",
        description = "readStatusId로 대상을 지정하여 요청 본문(JSON) 값으로 읽음 상태를 수정합니다.",
        operationId = "updateReadStatus"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = {@Content(schema = @Schema(implementation = ReadStatus.class)),
                @Content(mediaType = MediaType.ALL_VALUE) }
        ),
        @ApiResponse(responseCode = "404", description = "대상 리소스를 찾을 수 없음",
        content = @Content(mediaType = MediaType.ALL_VALUE)),
        @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 형식 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ReadStatus> update( // 수정 엔드포인트
        @PathVariable("readStatusId") UUID readStatusId, // PathVariable로 대상 지정
        @RequestBody ReadStatusUpdateRequest request // 수정 값 바인딩
    ) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request); // 서비스에 수정 위임
        return ResponseEntity
            .status(HttpStatus.OK) // 200 OK
            .body(updatedReadStatus); // 수정 결과 반환
    }

    @GetMapping( // 조회는 GET 메서드로 명시
        produces = "application/json" // 응답 본문 타입(JSON)
    )
    @Operation( // 조회 API 문서(간단 요약/설명만)
        summary = "사용자별 읽음 상태 목록 조회",
        description = "userId로 지정한 사용자의 전체 읽음 상태 목록을 조회합니다.",
        operationId = "findAllByUserId",
        parameters = {
            @Parameter(
                name = "userId",
                description = "조회할 사용자 ID",
                required = true,
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string", format = "uuid")
            )
        }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = {
                @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))),
                @Content(mediaType = MediaType.ALL_VALUE)
            }),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 형식 오류(예: UUID 파싱 실패)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ReadStatus>> findAllByUserId( // 사용자별 목록 조회 엔드포인트
        @Parameter(description = "조회할 사용자 ID", required = true) // ★ 문서에 필수로 노출
        @RequestParam(name = "userId", required = true) UUID userId // ★ 필수 쿼리 파라미터
    ) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId); // 서비스에 조회 위임
        return ResponseEntity
            .status(HttpStatus.OK) // 200 OK
            .body(readStatuses); // 조회 결과 리스트 반환
    }
}
