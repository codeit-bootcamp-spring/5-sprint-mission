package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  ReadStatus save(ReadStatus readStatus);

  Optional<ReadStatus> findById(UUID id);

  List<ReadStatus> findByUserId(UUID userId);

  List<ReadStatus> findByChannelId(UUID channelId);

  List<ReadStatus> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);

  User user(User user);
}
