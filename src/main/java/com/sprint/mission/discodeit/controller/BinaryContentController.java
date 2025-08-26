package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회")
    @GetMapping(value = "/{binaryContentId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "첨부 파일 조회 성공",
                    content = @Content(schema = @Schema(implementation = BinaryContentDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not fond"))
            )
    })
    public ResponseEntity<BinaryContentDto> findById(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @Operation(summary = "여러 첨부 파일 조회")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "첨부 파일 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)))
            )
    })
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto> binaryContent = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }
}
