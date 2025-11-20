package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.FileDownloadResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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

    private final BinaryContentStorage binaryContentStorage;

    private final BinaryContentMapper binaryContentMapper;

    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(Collection<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(binaryContentIds);

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Transactional(readOnly = true)
    public BinaryContentDto getBinaryContent(UUID binaryContentId) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    public FileDownloadResponse download(UUID binaryContentId) {
        BinaryContent content = getOrThrow(binaryContentId);

        Resource resource = binaryContentStorage.getResource(binaryContentId);

        return new FileDownloadResponse(
            resource,
            content.getFileName(),
            content.getContentType(),
            content.getSize()
        );
    }

    private BinaryContent getOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> {
                log.warn("파일을 찾을 수 없습니다. binaryContentId={}", binaryContentId);

                return new BinaryContentNotFoundException();
            });
    }
}
