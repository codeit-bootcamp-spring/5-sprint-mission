package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
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
