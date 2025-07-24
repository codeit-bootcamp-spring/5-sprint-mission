package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID channelId, UUID authorId, String content);
    Message find(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String content);
    void delete(UUID messageId);

    /**
     * 모든 메시지 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
