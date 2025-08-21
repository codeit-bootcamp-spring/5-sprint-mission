package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

  ReadStatus save(ReadStatus readStatus);

  void deleteByChannelId(UUID channelId);

  List<ReadStatus> findByChannelId(UUID channelId);

  //List<UUID> findChannelIdsByUserId(UUID userId);
  List<ReadStatus> findByUserId(UUID userId);

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  //List<UUID> findUserIdsByChannelId(UUID channelId);
  boolean existsById(UUID id);

  void deleteById(UUID id);

  Optional<ReadStatus> findById(UUID id);

  void deleteAllByChannelId(UUID id);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID uuid, UUID uuid1);
}
