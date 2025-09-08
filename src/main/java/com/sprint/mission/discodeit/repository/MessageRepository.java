package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Message 전용 레포지토리
 * - 채널 단위 조회 및 삭제 메서드만 선언
 */
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
    Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);
}
