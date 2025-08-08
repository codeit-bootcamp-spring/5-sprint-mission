package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    // C / U
    ReadStatus save(ReadStatus readStatus);

    // R
    Optional<ReadStatus> findById(UUID id);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAll();

    // 존재 여부
    boolean existsById(UUID id);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    // D
    boolean deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
    void deleteAllByChannelId(UUID channelId);

    // 테스트/초기화용 전체 삭제
    void deleteAll();
}