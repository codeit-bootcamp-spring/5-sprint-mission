package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

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
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  // ---- CRUD (JpaRepository 기본 제공이지만 과제 요구에 맞춰 명시) ----
  @Override
  ReadStatus save(ReadStatus readStatus);

  @Override
  Optional<ReadStatus> findById(UUID id);

  @Override
  List<ReadStatus> findAll();

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);

  // ---- 표준 파생 쿼리 (property path 권장) ----
  List<ReadStatus> findByUser_Id(UUID userId);
  List<ReadStatus> findByChannel_Id(UUID channelId);
  boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);
  void deleteByChannel_Id(UUID channelId);

  // ---- 기존 서비스 코드 호환 메서드 ----
  @Query("select rs from ReadStatus rs where rs.user.id = :userId")
  List<ReadStatus> findAllByUserId(@Param("userId") UUID userId);

  @Query("select rs from ReadStatus rs where rs.channel.id = :channelId")
  List<ReadStatus> findAllByChannelId(@Param("channelId") UUID channelId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("delete from ReadStatus rs where rs.channel.id = :channelId")
  void deleteAllByChannelId(@Param("channelId") UUID channelId);
}
