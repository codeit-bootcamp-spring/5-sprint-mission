package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  // ---- CRUD (JpaRepository가 제공하지만 과제 요구에 맞춰 명시) ----
  @Override
  Message save(Message message);

  @Override
  Optional<Message> findById(UUID id);

  @Override
  List<Message> findAll();

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);

  List<Message> findByChannel_Id(UUID channelId);
  Optional<Message> findTop1ByChannel_IdOrderByCreatedAtDesc(UUID channelId);

  @Query("select m from Message m where m.channel.id = :channelId")
  List<Message> findAllByChannelId(@Param("channelId") UUID channelId);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from Message m where m.channel.id = :channelId")
  void deleteAllByChannelId(@Param("channelId") UUID channelId);

  // 최신순 + Slice 페이징 (count 쿼리 X)
  Slice<Message> findByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);
}
