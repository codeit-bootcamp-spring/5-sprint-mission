package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, String userId, UUID channelId);

    // 전체 메세지 조회
    List<Message> getMessages();
    // 특정 메세지 조회
    String getMessageById(UUID id);
    // 채널 메세지 조회
    List<Message> getMessagesByChannel(UUID channelId);
    // 유저 메세지 조회
    List<Message> getMessagesByUser(String userId);
    // 수정
    boolean updateMessage(UUID messageId, String nContent);
    // 삭제
    boolean deleteMessage(UUID messageId);

}
