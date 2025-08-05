package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    // 생성
    Message createMessage(UUID senderId, UUID channelId, String name, String title, String content);

    // 읽기 & 모두 읽기
    Optional<Message> readMessage(UUID id);
    List<Message> readAllMessages();

    // 수정
    Message updateMessage(Message message);

    // 삭제
    void deleteMessage(UUID id);


}
