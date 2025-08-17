package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // ✅ 파일 생성 (등록)
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody BinaryContentCreateRequest request) {
        UUID id = binaryContentService.create(request);
        return ResponseEntity.ok(id);
    }


    // ✅ 파일 1개 조회
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> findById(@PathVariable UUID id) {
        BinaryContent file = binaryContentService.findById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }

    // ✅ 파일 여러개 조회
    @PostMapping("/batch")
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestBody List<UUID> ids) {
        List<BinaryContent> files = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(files);
    }

    // ✅ 파일 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        binaryContentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
