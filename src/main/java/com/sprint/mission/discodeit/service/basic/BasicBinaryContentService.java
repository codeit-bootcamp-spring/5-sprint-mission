package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentDeleteFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailedException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContent createBinaryContent(MultipartFile profile) {

    if (profile == null || profile.isEmpty()) {
      return null;
    }

    BinaryContent binaryContent = BinaryContent.builder()
        .fileName(profile.getOriginalFilename())
        .contentType(profile.getContentType())
        .size(profile.getSize())
        .status(BinaryContentStatus.PROCESSING)
        .build();

    BinaryContent saved = binaryContentRepository.save(binaryContent); // 이벤트 제외
    try {
      applicationEventPublisher.publishEvent(
          new BinaryContentCreatedEvent(saved.getId(), profile.getBytes()));
    } catch (Exception e) {
      throw BinaryContentSaveFailedException.withMessage(e.getMessage());
    }

    return saved;
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)    // BinaryContentStatus 트랜잭션 전파 범위
  public BinaryContentDTO updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(BinaryContentNotFoundException::new);

    binaryContent.updateStatus(status);
    binaryContentRepository.save(binaryContent);

    log.info("binaryContent status: {}", binaryContent.getStatus());

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDTO findByBinaryContentId(UUID binaryContentId) {
    BinaryContent save = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(BinaryContentNotFoundException::new);
    return binaryContentMapper.toDto(save);

  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDTO> findAllByIdIn(List<UUID> attachmentIds) {
    List<BinaryContent> saves = binaryContentRepository.findAllById(attachmentIds);
    return binaryContentMapper.toDto(saves);
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new BinaryContentDeleteFailedException();
    }
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Override
  @Transactional
  public void deleteAll(List<BinaryContent> attachmentIds) {
    if (attachmentIds == null || attachmentIds.isEmpty()) {
      return;
    }

    binaryContentRepository.deleteAll(attachmentIds);
  }
}
