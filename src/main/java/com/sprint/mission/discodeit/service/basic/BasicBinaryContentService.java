package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.exception.file.BinaryContentNotFoundException; // 🔹 추가
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.info("[FILE][UPLOAD] name={} size={} contentType={}", fileName, bytes.length, contentType);

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);

    log.info("[FILE][UPLOAD][DONE] id={} size={}", binaryContent.getId(), binaryContent.getSize());
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.info("[FILE][DOWNLOAD] id={}", binaryContentId);

    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContent -> {
          log.info("[FILE][DOWNLOAD][DONE] id={} size={}", binaryContentId, binaryContent.getSize());
          return binaryContentMapper.toDto(binaryContent);
        })
        .orElseThrow(() -> {
          log.warn("[FILE][DOWNLOAD] not-found id={}", binaryContentId);
          return new BinaryContentNotFoundException(binaryContentId);   // 🔹 교체
        });
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("[FILE][BULK-FIND] ids={}", binaryContentIds);
    return binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.info("[FILE][DELETE] id={}", binaryContentId);

    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("[FILE][DELETE] not-found id={}", binaryContentId);
      throw new BinaryContentNotFoundException(binaryContentId);       // 🔹 교체
    }
    binaryContentRepository.deleteById(binaryContentId);

    log.info("[FILE][DELETE][DONE] id={}", binaryContentId);
  }
}
