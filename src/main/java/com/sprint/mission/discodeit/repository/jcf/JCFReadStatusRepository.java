package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> data;

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID uuid, UUID uuid1) {
    return Optional.empty();
  }

  public JCFReadStatusRepository() {
    this.data = new HashMap<>();
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    this.data.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    this.data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
  }


  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    return this.data.values().stream().filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public List<ReadStatus> findByUserId(UUID userId) {
    return this.data.values().stream().filter(message -> message.getUserId().equals(userId))
        .toList();
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(this.data.get(id));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return this.data.values().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return this.data.values().stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return this.data.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    this.data.remove(id);
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    deleteByChannelId(channelId);
  }

}
