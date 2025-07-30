package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 생성
    Message createMessage(UUID senderId, UUID channelId, String name, String title, String content);

    // 읽기 & 모두 읽기
    Message readMessage(UUID id);
    List<Message> readAllMessages();

    // 수정
    Message updateName(UUID id, String name);
    Message updateTitle(UUID id, String title);
    Message updateContent(UUID id, String content);

    // 삭제
    boolean deleteMessage(UUID id);


}
