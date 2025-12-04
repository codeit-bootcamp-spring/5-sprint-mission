package com.sprint.mission.discodeit.domain.controller;

import com.sprint.mission.discodeit.domain.docs.BinaryContentControllerDocs;
import com.sprint.mission.discodeit.domain.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.domain.service.BinaryContentService;
import com.sprint.mission.discodeit.infra.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentControllerDocs {

    private final BinaryContentService binaryContentService;

    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    public List<BinaryContentDto> findAllById(@RequestParam Collection<UUID> binaryContentIds) {
        return binaryContentService.findAllById(binaryContentIds);
    }

    @GetMapping("/{binaryContentId}")
    public BinaryContentDto find(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }

    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

        return binaryContentStorage.download(binaryContentDto);
    }
}
