package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID userId, UUID chId, String messageKey, String message);
    Message findMessageKey(String messageKey); //메시지 키 기반 조회
    List<Message> findAll(); //모든 메시지 조회
    List<Message> findByUserId(UUID userId); //유저별 메시지 목록 조회
    void update(String messageKey, String newMessage);
    void delete(String messageKey);
}