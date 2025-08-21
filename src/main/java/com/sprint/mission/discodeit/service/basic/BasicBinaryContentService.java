package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContentResponse create(BinaryContentCreateRequest request) {
    BinaryContent content = new BinaryContent(
        request.fileName(),
        (long) (request.bytes() != null ? request.bytes().length : 0),
        request.contentType(),
        request.bytes()
    );

    BinaryContent saved = binaryContentRepository.save(content);
    return new BinaryContentResponse(
        saved.getId(),
        saved.getFileName(),
        saved.getContentType(),
        saved.getBytes()
    );
  }

  @Override
  public BinaryContent findById(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Override
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .toList();
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new NoSuchElementException("BinaryContent not found: " + id);
    }
    binaryContentRepository.deleteById(id);
  }
}
