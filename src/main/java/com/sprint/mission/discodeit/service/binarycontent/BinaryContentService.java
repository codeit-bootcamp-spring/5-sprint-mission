package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  public BinaryContentResponse create(BinaryContentCreateRequest req) {
    BinaryContent saved = binaryContentRepository.save(new BinaryContent(
        req.filename(),
        req.contentType(),
        req.bytes()
    ));

    return BinaryContentResponse.from(saved);
  }

  public BinaryContentResponse findById(UUID id) {
    return BinaryContentResponse.from(binaryContentRepository.getOrThrow(id));

  }

  public List<BinaryContentResponse> findAll() {
    return binaryContentRepository.findAll().stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  public List<BinaryContentResponse> findAllById(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return binaryContentRepository.findAllById(ids).stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  public boolean delete(UUID id) {
    if (id == null) {
      return false;
    }
    return binaryContentRepository.softDeleteById(id);
  }
}
