package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

  protected final Map<UUID, BinaryContent> data;

  public JCFBinaryContentRepository() {
    data = new HashMap<>();
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    data.put(binaryContent.getId(), binaryContent);
    return data.get(binaryContent.getId());
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.of(data.get(id));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return data.values().stream()
        .filter(bc -> ids.contains(bc.getId()))
        .toList();
  }

  @Override
  public void delete(UUID id) {
    data.remove(id);
  }

  @Override
  public void deleteAll() {
    data.clear();
  }
}
