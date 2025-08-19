package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "BinaryContent API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    @Operation(summary = "단건 파일 조회", description = "한 개의 파일을 가져옵니다.")
    public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @GetMapping
    @Operation(summary = "다건 파일 조회", description = "전체 파일의 목록을 가져옵니다.")
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
        @RequestParam
        @Parameter(
            description = "binaryContentIds",
            required = true
        )
        List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            throw new IllegalArgumentException("binaryContentIds가 필요합니다.");
        }

        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
    }
}
