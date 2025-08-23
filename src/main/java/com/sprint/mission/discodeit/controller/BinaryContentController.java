package com.sprint.mission.discodeit.controller; // 컨트롤러가 속한 패키지 선언

import com.sprint.mission.discodeit.entity.BinaryContent; // 바이너리 컨텐츠 엔티티 임포트
import com.sprint.mission.discodeit.service.BinaryContentService; // 바이너리 컨텐츠 서비스 임포트
import lombok.RequiredArgsConstructor; // final 필드 생성자 자동 생성(Lombok)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // 응답을 감싸는 ResponseEntity
import org.springframework.web.bind.annotation.*; // REST 컨트롤러 관련 애너테이션 임포트

import java.util.List; // 리스트 컬렉션
import java.util.UUID; // UUID 타입

// --- Swagger(OpenAPI) 임포트 ---
import io.swagger.v3.oas.annotations.Operation; // 엔드포인트 요약/설명
import io.swagger.v3.oas.annotations.media.Content; // 요청/응답 콘텐츠
import io.swagger.v3.oas.annotations.media.Schema; // 스키마 정의
import io.swagger.v3.oas.annotations.media.ArraySchema; // 배열 스키마
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 응답 코드 정의
import io.swagger.v3.oas.annotations.responses.ApiResponses; // 복수 응답 정의
import io.swagger.v3.oas.annotations.tags.Tag; // 컨트롤러 태그
// ------------------------------

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "바이너리 컨텐츠 조회 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    @Operation(summary = "바이너리 컨텐츠 단건 조회", description = "경로의 binaryContentId로 특정 바이너리 컨텐츠를 조회합니다.", operationId = "findBinaryContent")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BinaryContent.class))
        ),
        @ApiResponse(responseCode = "404", description = "대상 리소스를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContent);
    }

    @GetMapping // ("/findAllByIdIn")
    @Operation(summary = "바이너리 컨텐츠 복수 조회", description = "모든 바이너리 컨텐츠를 조회합니다.", operationId = "findBinaryContentsByIdIn")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class)))
        ),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
        @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContents);
    }
}
