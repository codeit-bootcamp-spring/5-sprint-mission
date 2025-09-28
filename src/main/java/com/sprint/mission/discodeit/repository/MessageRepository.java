package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
//    void deleteByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID id);
//    Optional<Instant> findMostRecentMessageTime(UUID channelId);
=======
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author a "
      + "JOIN FETCH a.status "
      + "LEFT JOIN FETCH a.profile "
      + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
  Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable);


  @Query("SELECT m.createdAt "
      + "FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "ORDER BY m.createdAt DESC LIMIT 1")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
