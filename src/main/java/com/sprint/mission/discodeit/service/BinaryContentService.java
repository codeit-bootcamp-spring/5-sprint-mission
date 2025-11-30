package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final BinaryContentMapper binaryContentMapper;

    @Transactional
    public BinaryContentDto create(MultipartFile file) {
        log.debug("바이너리 콘텐츠 생성 시도: filename={}, size={}",
            file.getOriginalFilename(), file.getSize());

        BinaryContent binaryContent = binaryContentRepository.save(
            new BinaryContent(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            )
        );

        try {
            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContent.getId(), file.getBytes())
            );
        } catch (IOException e) {
            throw new BinaryContentUploadException(e);
        }

        log.info("바이너리 콘텐츠 생성 완료: binaryContentId={}", binaryContent.getId());

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(Collection<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = getOrThrow(binaryContentId);

        return binaryContentMapper.toDto(binaryContent);
    }

    private BinaryContent getOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }
}
