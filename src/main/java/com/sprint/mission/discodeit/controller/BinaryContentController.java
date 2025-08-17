package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.global.api.ApiResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/files")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 1개 조회
    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<BinaryContent>> find(@PathVariable("fileId") UUID binaryContentId) {
        BinaryContent bc = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(ApiResponse.ok(bc));
    }

    // 여러 개 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BinaryContent>>> findAll(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContent> list = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    // (옵션) 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable("fileId") UUID binaryContentId) {
        binaryContentService.delete(binaryContentId);
        return ResponseEntity.noContent().build();
    }
}

