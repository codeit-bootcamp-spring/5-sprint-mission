package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  // user.id 로 조회
  List<ReadStatus> findAllByUser_Id(UUID userId);

  // channel.id 로 조회
  List<ReadStatus> findAllByChannel_Id(UUID channelId);

  // channel.id 로 일괄 삭제
  void deleteAllByChannel_Id(UUID channelId);

  // (user.id, channel.id)로 단건 조회
  Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);
}
