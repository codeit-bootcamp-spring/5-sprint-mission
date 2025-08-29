package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentRepository binaryContentRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BinaryContentDto> findAllByIn(
        @RequestParam("binaryContentIds") Set<UUID> binaryContentIds) {
        return binaryContentRepository.findAllToDtoByIdIn(binaryContentIds);
    }

    @GetMapping("/{binaryContentId}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryContentDto find(@PathVariable UUID binaryContentId) {
        return BinaryContentDto.from(binaryContentRepository.getOrThrow(binaryContentId));
    }

    @GetMapping("/{binaryContentId}/download")
    @ResponseStatus(HttpStatus.OK)
    public byte[] download(@PathVariable UUID binaryContentId) {
        return binaryContentRepository.findBytesById(binaryContentId);
    }
}
