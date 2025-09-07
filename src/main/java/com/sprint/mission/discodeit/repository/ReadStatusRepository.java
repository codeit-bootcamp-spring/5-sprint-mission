package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID>{

  ReadStatus save(ReadStatus readStatus);

  void deleteByChannelId(UUID channelId);
    List<ReadStatus> findByChannelId(UUID channelId);
    List<ReadStatus> findByUserId(UUID userId);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  Optional<ReadStatus> findById(UUID id);
}
