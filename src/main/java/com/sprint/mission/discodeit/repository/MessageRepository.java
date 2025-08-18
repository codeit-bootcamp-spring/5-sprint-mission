package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    List<Message> findByChannel(UUID channelId);

    List<Message> findAll();

    Optional<Message> findById(UUID id);

    List<Message> findByContent(String str);

    Optional<Instant>  findLastCreatedAtByChannelId(UUID channelId);

    /**
     * 해당 채널의 모든 메시지 조회
     * - 처음 접속한 사용자 등
     */
    List<Message> findAllByChannelId(UUID channelId);

    /**
     * 특정 시점 이후의 메시지만 조회
     * - 읽음 이후의 새 메시지
     */
    List<Message> findAllByChannelIdAfter(UUID channelId, Instant after);

    boolean deleteById(UUID id);
}
