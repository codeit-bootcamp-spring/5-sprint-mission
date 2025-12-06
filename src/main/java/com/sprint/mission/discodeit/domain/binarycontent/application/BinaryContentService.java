package com.sprint.mission.discodeit.domain.binarycontent.application;

import com.sprint.mission.discodeit.domain.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.domain.binarycontent.domain.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.domain.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    public List<BinaryContentDto> findAllById(Collection<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Cacheable(value = CacheName.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @CachePut(value = CacheName.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus newStatus) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);
        BinaryContentStatus oldStatus = binaryContent.getStatus();

        binaryContent.updateStatus(newStatus);

        return binaryContentMapper.toDto(binaryContent);
    }

    private BinaryContent getOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }
}
