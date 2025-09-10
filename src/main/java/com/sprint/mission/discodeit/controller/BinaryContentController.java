package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.sub.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 콘텐츠 단일 조회
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    // 바이너리 콘텐츠 복수 조회
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds) {
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(binaryContents);
    }

    // 바이너리 콘텐츠 원본 다운로드
    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.findEntity(binaryContentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContent.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(binaryContent.getContentType()))
                .body(binaryContent.getBytes());
    }
}