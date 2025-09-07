package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ReadStatus 전용 레포지토리
 * - 사용자/채널 단위 조회 및 삭제 메서드 선언
 */
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
}
