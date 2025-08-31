package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BinaryContentDto> findAllByIn(@RequestParam Set<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds);
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
    @ResponseStatus(HttpStatus.OK)
    public byte[] download(@PathVariable UUID binaryContentId) {
        return binaryContentRepository.findBytesById(binaryContentId);
    }
}
