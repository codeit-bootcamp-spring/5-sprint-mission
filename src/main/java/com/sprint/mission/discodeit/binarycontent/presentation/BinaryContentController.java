package com.sprint.mission.discodeit.binarycontent.presentation;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentService;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentControllerDocs {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @GetMapping
    public List<BinaryContentDto> findAllById(@RequestParam Set<UUID> binaryContentIds) {
        return binaryContentService.findAllById(binaryContentIds);
    }

    @GetMapping("/{binaryContentId}")
    public BinaryContentDto find(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }

    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<Void> download(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);
    }
}
