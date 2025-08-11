package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/binary")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> findById(@PathVariable UUID id) {
        BinaryContent bc = binaryContentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
        "inline; filename=\"" + bc.getFileName() + "\"")
                .body(bc.getContent());
    }
}
