package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinaryContentService {

    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentRepository binaryContentRepository;

    public List<BinaryContentDto> findAllById(Collection<UUID> binaryContentIds) {
        log.debug("Finding all binary contents: [binaryContentIds={}]", binaryContentIds);

        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Cacheable(value = CacheName.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("Finding binary content: [binaryContentId={}]", binaryContentId);

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @CachePut(value = CacheName.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus newStatus) {
        log.info("Attempting to update binary content status: [binaryContentId={}]", binaryContentId);

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        BinaryContentStatus oldStatus = binaryContent.getStatus();
        binaryContent.updateStatus(newStatus);

        BinaryContentDto result = binaryContentMapper.toDto(binaryContent);

        log.info("Binary content status updated: [binaryContentId={}, oldStatus={}, newStatus={}]",
            binaryContentId, oldStatus, newStatus);

        return result;
    }
}
