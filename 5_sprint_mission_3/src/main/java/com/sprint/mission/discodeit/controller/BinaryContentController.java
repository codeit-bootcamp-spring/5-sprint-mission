package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(path = "find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        Optional<BinaryContent> binaryContent = binaryContentService.findById(binaryContentId);
        return binaryContent.map(content -> ResponseEntity.status(200).body(content))
                .orElseGet(() -> ResponseEntity.status(404).build());
    } // ResponseEntity가 BinaryContent를 반환, 존재할 경우 200 OK, 존재하지 않을 경우 404 에러 반환.

    @RequestMapping(path = "findMultiple", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findMultiple(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(contents);
    }
}