package com.sprint.mission.discodeit.domain.service;

import com.sprint.mission.discodeit.common.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.domain.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.domain.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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

    public List<BinaryContentDto> findAllById(Collection<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);

        return binaryContents.stream().map(binaryContentMapper::toDto).toList();
    }

    @Cacheable(value = CacheType.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @CacheEvict(value = CacheType.BINARY_CONTENTS, key = "#binaryContentId")
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus newStatus) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);

        log.debug("파일 상태 변경 요청: {}", binaryContentId);

        BinaryContentStatus oldStatus = binaryContent.getStatus();
        binaryContentRepository.save(binaryContent.updateStatus(newStatus));
        log.info("파일 상태 변경: {} -> {} (파일 ID: {})", oldStatus, newStatus, binaryContentId);

        return binaryContentMapper.toDto(binaryContent);
    }

    private BinaryContent getOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }
}
