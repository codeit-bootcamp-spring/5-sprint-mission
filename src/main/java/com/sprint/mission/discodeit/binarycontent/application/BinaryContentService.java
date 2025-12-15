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

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllById(Collection<UUID> binaryContentIds) {
        log.debug("Finding all binary contents in: [binaryContentIds={}]", binaryContentIds);

        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.BINARY_CONTENT, key = "#binaryContentId")
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("[Cache Miss] find binary content by: [binaryContentId={}]", binaryContentId);

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @CachePut(value = CacheName.BINARY_CONTENT, key = "#binaryContentId")
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus newStatus) {
        log.debug("Updating binary content status: [binaryContentId={}]", binaryContentId);

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
