package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("binaryContentService")
@RequiredArgsConstructor
@Validated
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(@Valid BinaryContentCreateRequest binaryContentCreateRequest) {
    BinaryContent binaryContent = new BinaryContent(binaryContentCreateRequest.fileName(),
        binaryContentCreateRequest.contentType(), binaryContentCreateRequest.bytes());
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  public BinaryContent findById(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("findById : BinaryContent를 찾을 수 없습니다."));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAll().stream()
        .filter(binaryContent -> ids.contains(binaryContent.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new NoSuchElementException("delete : BinaryContent를 찾을 수 없습니다.");
    }
    binaryContentRepository.deleteById(id);
  }
}
