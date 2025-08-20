package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/find")
    public ResponseEntity<BinaryContentResponse> find(@RequestParam UUID binaryContentId) {
        BinaryContentResponse binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<BinaryContentResponse>> findAll(@RequestParam List<UUID> binaryContentIds) {
        List<BinaryContentResponse> contents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(contents);
    }
}