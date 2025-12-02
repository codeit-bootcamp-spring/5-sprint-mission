package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<BinaryContentDto> findAllByIdIn(Collection<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);

        return binaryContents.stream().map(binaryContentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus newStatus) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);
        binaryContent.updateStatus(newStatus);
        return binaryContentMapper.toDto(binaryContent);
    }

    private BinaryContent getOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }
}
