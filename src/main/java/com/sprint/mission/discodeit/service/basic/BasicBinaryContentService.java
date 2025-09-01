package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("binaryContentService")
@RequiredArgsConstructor
@Validated
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContent create(@Valid NewBinaryContent newBinaryContent) {
    String fileName = newBinaryContent.fileName();
    String contentType = newBinaryContent.contentType();
    byte[] bytes = newBinaryContent.bytes();
    long size = bytes.length;

    BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);

    binaryContentStorage.put(binaryContent.getId(), bytes);
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto findById(UUID id) {
    return binaryContentRepository.findById(id)
        .map(binaryContentMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("findById : BinaryContent를 찾을 수 없습니다. [" + id + "]"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAll().stream()
        .filter(binaryContent -> ids.contains(binaryContent.getId()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new NoSuchElementException("delete : BinaryContent를 찾을 수 없습니다. [" + id + "]");
    }
    binaryContentRepository.deleteById(id);
  }
}
