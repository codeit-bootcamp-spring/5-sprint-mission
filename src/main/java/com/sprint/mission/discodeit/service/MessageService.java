package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 추가
    Message create(String content, UUID userId, UUID channelId);

    // 메시지 조회
    Message find(UUID messageId);

    // 메시지 전체 조회
    List<Message> findAll();

    // 메시지 수정
    Message update(UUID messageId, String content);

    // 메시지 삭제
    boolean delete(UUID messageId);
}
