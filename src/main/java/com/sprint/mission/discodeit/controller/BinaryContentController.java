package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(path = "find")
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @RequestMapping(path = "findAll")
    public ResponseEntity<List<BinaryContent>> findAll(@RequestParam("binaryContentId") UUID binaryContentId) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(List.of(binaryContentId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}
