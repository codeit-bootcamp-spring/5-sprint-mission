package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.FileResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "BinaryContent API")
@RequestMapping("api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /** 파일 1개 조회 */
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<FileResponse> findById(@PathVariable UUID binaryContentId) {
        BinaryContent file = binaryContentService.findById(binaryContentId);
        return ResponseEntity.ok(FileResponse.of(file));
    }

    @GetMapping()
    public ResponseEntity<List<FileResponse>> findAllById(@RequestParam List<UUID> binaryContentIds) {
        List<BinaryContent> files = binaryContentService.findAllById(binaryContentIds);
        List<FileResponse> result = files.stream().map(FileResponse::of).toList();
        return ResponseEntity.ok(result);
    }
}
