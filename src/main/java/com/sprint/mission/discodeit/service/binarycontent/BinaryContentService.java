package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  public List<BinaryContentResponse> findAllByIn(List<UUID> binaryContentIds) {

    List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(binaryContentIds);
    if (binaryContents.size() != binaryContentIds.size()) {
      Set<UUID> found = binaryContents.stream().map(BinaryContent::getId)
          .collect(Collectors.toSet());
      UUID missing = binaryContentIds.stream().filter(id -> !found.contains(id)).findFirst()
          .orElse(null);
      throw new NotFoundException(
          "Binary content with id %s not found".formatted(missing));
    }

    return binaryContents.stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  public BinaryContentResponse find(UUID binaryContentId) {
    return BinaryContentResponse.from(binaryContentRepository.getOrThrow(binaryContentId));

  }
}
