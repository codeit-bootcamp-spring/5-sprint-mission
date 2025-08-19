package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {

  Map<UUID, BinaryContent> binaryContentMap;

  public JCFBinaryContentRepository() {
    binaryContentMap = new HashMap<>();
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    binaryContentMap.put(binaryContent.getId(), binaryContent);
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(binaryContentMap.get(id));
  }

  @Override
  public List<BinaryContent> findAll() {
    return new ArrayList<>(binaryContentMap.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return binaryContentMap.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    binaryContentMap.remove(id);
  }
}
