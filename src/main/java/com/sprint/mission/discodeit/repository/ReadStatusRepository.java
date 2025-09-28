package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
<<<<<<< HEAD
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
=======
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {


  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("SELECT r FROM ReadStatus r "
      + "JOIN FETCH r.user u "
      + "JOIN FETCH u.status "
      + "LEFT JOIN FETCH u.profile "
      + "WHERE r.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

  Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

  void deleteAllByChannelId(UUID channelId);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
