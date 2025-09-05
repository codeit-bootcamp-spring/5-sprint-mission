package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.BinaryContentControllerDocs;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentControllerDocs {

    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BinaryContentDto> findAllByIn(@RequestParam Set<UUID> binaryContentIds) {
        return binaryContentRepository.findAllToDtoByIdIn(binaryContentIds);
    }

    @GetMapping("/{binaryContentId}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryContentDto find(@PathVariable UUID binaryContentId) {
        return binaryContentRepository.getOrThrowToDto(binaryContentId);
    }

    @GetMapping(
        path = "/{binaryContentId}/download",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) {
        return binaryContentStorage.download(
            binaryContentRepository.getOrThrowToDto(binaryContentId)
        );
    }
}
