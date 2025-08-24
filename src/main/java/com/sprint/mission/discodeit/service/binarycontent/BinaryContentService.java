package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  public List<BinaryContentResponse> findAllByIn(List<UUID> binaryContentIds) {

    Map<UUID, BinaryContent> found = binaryContentRepository.findAllByIdIn(binaryContentIds)
        .stream()
        .collect(Collectors.toMap(
            BinaryContent::getId,
            Function.identity(),
            (a, b) -> a)
        );

    UUID missing = binaryContentIds.stream()
        .filter(id -> !found.containsKey(id))
        .findFirst()
        .orElse(null);

    if (missing != null) {
      throw new NotFoundException("Binary content with id %s not found".formatted(missing));
    }

    return binaryContentIds.stream()
        .map(found::get)
        .map(BinaryContentResponse::from)
        .toList();
  }

  public BinaryContentResponse find(UUID binaryContentId) {
    return BinaryContentResponse.from(binaryContentRepository.getOrThrow(binaryContentId));
  }
}
