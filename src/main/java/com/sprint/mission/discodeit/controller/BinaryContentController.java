package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.FileResponseDto;
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

import java.util.Base64;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /**
     * 바이너리 파일을 1개 조회
     */
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<FileResponseDto> findById(@PathVariable UUID binaryContentId) {
        BinaryContent file = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(
            FileResponseDto.builder()
            .id(file.getId())
            .createdAt(file.getCreatedAt())
            .fileName(file.getFileName())
            .size(file.getContent().length)
            .contentType(file.getContentType())
            .bytes(Base64.getEncoder().encodeToString(file.getContent()))
            .build()
        );
    }
}
