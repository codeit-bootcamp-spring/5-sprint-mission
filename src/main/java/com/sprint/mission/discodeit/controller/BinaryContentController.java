package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> findById(@PathVariable("id") UUID id) {
        BinaryContent binaryContent = binaryContentService.find(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + binaryContent.getFileName() + "\"")
                .contentLength(binaryContent.getSize())
                .body(binaryContent.getBytes());
    }
}
