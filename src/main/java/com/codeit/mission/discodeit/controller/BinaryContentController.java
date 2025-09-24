package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.BinaryContentApi;
import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.service.BinaryContentService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(path = "{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(
                binaryContentIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);
    }
}
