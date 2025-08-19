package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.service.BinaryContentService;
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
    public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
        @RequestParam List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            throw new IllegalArgumentException("binaryContentIds가 필요합니다.");
        }

        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
    }
}
