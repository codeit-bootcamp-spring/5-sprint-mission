package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  // ---- 표준 파생 쿼리 (property path 사용 권장) ----
  List<Message> findByChannel_Id(UUID channelId);
  Optional<Message> findTop1ByChannel_IdOrderByCreatedAtDesc(UUID channelId);

  // ---- 기존 서비스 코드 호환 메서드 ----
  @Query("select m from Message m where m.channel.id = :channelId")
  List<Message> findAllByChannelId(@Param("channelId") UUID channelId);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from Message m where m.channel.id = :channelId")
  void deleteAllByChannelId(@Param("channelId") UUID channelId);
}
